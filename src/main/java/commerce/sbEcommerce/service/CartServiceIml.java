package commerce.sbEcommerce.service;

import commerce.sbEcommerce.exceptioons.APIException;
import commerce.sbEcommerce.exceptioons.ResourceNotFoundException;
import commerce.sbEcommerce.exceptioons.UnauthorizedException;
import commerce.sbEcommerce.model.Cart;
import commerce.sbEcommerce.model.CartItem;
import commerce.sbEcommerce.model.Product;
import commerce.sbEcommerce.model.User;
import commerce.sbEcommerce.payload.CartDTO;
import commerce.sbEcommerce.payload.CartItemDTO;
import commerce.sbEcommerce.payload.ProductDTO;
import commerce.sbEcommerce.payload.ProductResponse;
import commerce.sbEcommerce.repository.CartItemRepository;
import commerce.sbEcommerce.repository.CartRepository;
import commerce.sbEcommerce.repository.ProductRepository;
import commerce.sbEcommerce.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Service
public class CartServiceIml implements CartService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = getOrCreateUserCart();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository
                .findByProduct_ProductIdAndCart_CartId(productId, cart.getCartId())
                .orElse(null);

        if(cartItem != null){
            throw new RuntimeException("Product is already exist! ");
        }

        if(product.getQuantity() == 0){
            throw new RuntimeException("Product is not available! ");

        }

        if(product.getQuantity() < quantity){
            throw new RuntimeException("Please, make an order less than or equal the quantity available ! ");
        }


        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getPrice());

        cartItemRepository.save(newCartItem);

       // product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice()*quantity) );


        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream= cartItems.stream()
                .map(item -> {
                    ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
                    map.setQuantity(item.getQuantity());
                    return map;
                });
        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;

    }


    @Override
    public Cart getOrCreateUserCart() {
        // Lấy entity User đang đăng nhập
        User user = authUtil.getCurrentUserEntity();

        // Tìm giỏ hàng theo người dùng
        Cart userCart = cartRepository.findByUser_Email(user.getEmail());

        if (userCart == null) {
            // Nếu chưa có thì tạo mới
            userCart = new Cart();
            userCart.setUser(user);
            userCart.setTotalPrice(0.0);
            userCart = cartRepository.save(userCart); // lưu vào DB
        }

        return userCart;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.size() == 0){
            throw new APIException("No cart exist");
        }

        List<CartDTO> cartDTOs = carts.stream().map((cart) ->{
            CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                    .collect(Collectors.toList());
            cartDTO.setProducts(products);
            return cartDTO;
        }).collect(Collectors.toList());
        return cartDTOs;
    }

    @Override
    public CartDTO getCart(String userEmail, Long cartId) {
        Cart cart = cartRepository.findByUser_EmailAndCartId(userEmail, cartId);
        if(cart == null){
            throw  new RuntimeException("Khong tim thay cart cua nguoi dung");
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
      //  cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity()); // 👉 Lấy quantity từ CartItem
            return productDTO;
        }).toList();

        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String userEmail = authUtil.getCurrentUserEmail();
        Cart cart = cartRepository.findByUser_Email(userEmail);
        if (cart == null) throw new RuntimeException("Cart not found");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() < quantity && quantity > 0) {
            throw new RuntimeException("Please, make an order less than or equal the quantity available!");
        }

        CartItem cartItem = cartItemRepository
                .findByProduct_ProductIdAndCart_CartId(productId, cart.getCartId())
                .orElseThrow(() -> new APIException("Product " + product.getProductName() + " not available!"));

        int newQuantity = cartItem.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new RuntimeException("Quantity cannot be negative!");
        }

        if (newQuantity == 0) {
            cart.getCartItems().remove(cartItem);
            cartItem.setCart(null);
            cartItemRepository.deleteById(cartItem.getCartItemId());
        }
        else {
            cartItem.setQuantity(newQuantity);
            cartItem.setProductPrice(product.getPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }

        // Update total cart price
        double total = cart.getCartItems().stream()
                .mapToDouble(item -> (item.getProductPrice()*item.getQuantity() - item.getProductPrice() * item.getQuantity()*item.getDiscount()*0.01))
                .sum();
        cart.setTotalPrice(total);
        cartRepository.save(cart);

        // Map to DTO
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(item -> {
                    ProductDTO dto = modelMapper.map(item.getProduct(), ProductDTO.class);
                    dto.setQuantity(item.getQuantity());
                    return dto;
                }).toList();

        cartDTO.setProducts(productDTOs);
        return cartDTO;
    }


    @Transactional
    @Override
    public String deleteProductFromCart( Long productId) {
        String userEmail= authUtil.getCurrentUserEmail();
        Cart userCart = cartRepository.findByUser_Email(userEmail);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart","CartId",cartId));

        CartItem cartItem = cartItemRepository.findByProduct_ProductIdAndCart_CartId(productId,cartId).orElse(null);
        System.out.println("User email: " + userEmail);
        System.out.println("Cart ID: " + cartId);

        if (cartItem == null) {
            System.out.println("Không tìm thấy cartItem với productId " + productId + " và cartId " + cartId);
            throw new ResourceNotFoundException("Product","ProductId",productId);
        }
        System.out.println("\n");
        System.out.println(cart.getTotalPrice());
        System.out.println(cartItem.getProductPrice());
        System.out.println(cartItem.getQuantity());
        System.out.println(cartItem.getDiscount() );

        Double newTotal = cart.getTotalPrice() - (cartItem.getProductPrice() - cartItem.getProductPrice() * cartItem.getQuantity()*cartItem.getDiscount() * 0.01);

        cart.setTotalPrice(newTotal);
        String productName = cartItem.getProduct().getProductName();

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);
        return "Sản phẩm " + productName + " đã bị xóa khỏi giỏ hàng";

    }
}

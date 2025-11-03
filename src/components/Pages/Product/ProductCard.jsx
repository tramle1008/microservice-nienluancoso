import { useState } from "react";
import { FaShoppingCart } from "react-icons/fa";
import ProductView from "./ProductView";
import { useDispatch } from "react-redux"

import toast from "react-hot-toast";
import AddProductToCart from "../Cart/AddProductToCart";
const ProductCard = ({ productId,
    productName,
    image,
    description,
    quantity,
    price,
    discount,
    specialPrice
}) => {
    const [openProductView, setOpentProductView] = useState(false);
    const loader = false;
    const [selectViewProduct, setSelectViewProduct] = useState(null);
    const isAvailable = Number(quantity) > 0;
    const dispatch = useDispatch();


    const handleProductViewModel = (product) => {
        setSelectViewProduct(product);
        setOpentProductView(true);
    }
    const addToCartHandle = async () => {
        const success = AddProductToCart(productId, 1);

    };

    return (
        <div className="border rounded-lg shadow-xl overflow-hidden transition-shadow duration-300 bg-white">
            <div onClick={() => {
                handleProductViewModel({
                    productId,
                    productName,
                    image,
                    description,
                    quantity,
                    price,
                    discount,
                    specialPrice
                })
            }} className="w-full overflow-hidden aspect-square">
                <img className="w-full h-full cursor-pointer transition-transform duration-300 transform hover:scale-105" src={image} alt={productName} />
            </div>
            <div className="p-4">
                <h2
                    className="text-lg font-semibold"
                >{productName}</h2>
            </div>
            <div className="min-h-20 max-h-20">
                <p className="text-gray-400 text-sm px-2">{description}</p>
            </div>
            <div className="flex items-center justify-between">

                {specialPrice ? (
                    <div className="flex flex-col font-bold text-sla">
                        <span className="text-red-500 text-lg px-2">
                            Giảm {Number(discount)}%
                        </span>
                        <span className="text-gray-700 p-2 line-through">
                            {Number(price).toLocaleString()} vnđ
                        </span>
                        <span className="text-gray-700 p-2">
                            {Number(specialPrice).toLocaleString()} vnđ
                        </span>
                    </div>

                ) : (
                    <div className="flex flex-col font-bold text-sla">
                        <span className="text-gray-700 p-2">
                            {Number(price).toLocaleString()} vnđ
                        </span>
                    </div>
                )
                }

                <button
                    className={`bg-emerald-700 text-white py-2 px-2 mr-2 rounded-lg items-center transition-colors duration-300 w-36 flex justify-center ${isAvailable ? "opacity-100 hover:bg-emerald-900" : "opacity-70"}`}
                    disabled={!isAvailable}
                    onClick={addToCartHandle}
                >
                    <FaShoppingCart className="mr-3" />
                    {isAvailable ? "Add" : "Stock out"}
                </button>
            </div>
            <ProductView
                open={openProductView}
                setOpen={setOpentProductView}
                product={selectViewProduct}
                isAvailable={isAvailable}
            />
        </div >

    );
};
export default ProductCard;
//// src/store/actions/index.js
import api from "../../api/api";
import axios from "axios";
export const fetchProduct = (queryString) => async (dispatch) => {
    try {
        const { data } = await api.get(`/public/products?${queryString}`);

        dispatch({
            type: "FETCH_PRODUCTS",
            payload: {
                products: data.content,
                pageNumber: data.pageNumber,
                pageSize: data.pageSize,
                totalElements: data.totalElements,
                totalPages: data.totalPages,
                lastPage: data.lastPage,
            },
        });
    } catch (error) {
        console.error("Lỗi khi fetch sản phẩm:", error);
    }
};

export const fetchCategories = () => async (dispatch) => {
    try {
        const { data } = await api.get(`/public/categories`);

        dispatch({
            type: "FETCH_CATEGORIES",
            payload: data.content,
        });
    } catch (error) {
        console.error("Lỗi khi fetch category:", error);
    }
};


export const fetchAddresses = () => async (dispatch) => {
    dispatch({ type: "ADDRESS_FETCH_REQUEST" });

    try {
        const auth = JSON.parse(localStorage.getItem("auth"));
        const token = auth?.jwtToken;

        const res = await api.get("/auth/user/addresses", {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        dispatch({
            type: "ADDRESS_FETCH_SUCCESS",
            payload: res.data
        });
    } catch (error) {
        dispatch({
            type: "ADDRESS_FETCH_FAIL",
            payload: error.response?.data?.message || error.message,
        });
    }
};

//fetchCart
export const fetchCart = () => async (dispatch) => {
    dispatch({ type: "CART_REQUEST" });

    try {
        const auth = localStorage.getItem("auth");
        const token = JSON.parse(auth)?.jwtToken;

        const res = await api.get("/auth/carts/user/cart", {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        dispatch({
            type: "CART_SUCCESS",
            payload: res.data,
        });
    } catch (err) {
        dispatch({
            type: "CART_FAILURE",
            payload: "Không thể tải giỏ hàng",
        });
    }
};


export const addToCart = (data, qty = 1, toast) =>
    (dispatch, getState) => {
        const { products } = getState().products; // ✅ đúng

        const getProduct = products.find(
            (item) => item.productId === data.productId
        );

        if (!getProduct) {
            console.warn("Không tìm thấy sản phẩm với ID:", data.productId);
            return;
        }

        const isQuantityExist = getProduct.quantity >= qty;

        if (isQuantityExist) {
            dispatch({
                type: "ADD_CART",
                payload: {
                    ...data,
                    quantity: qty
                }
            });
            toast.success(`${data?.productName} đã được thêm vào giỏ hàng`);
            localStorage.setItem("cartItems", JSON.stringify(getState().cart.cart));
        } else {
            toast.error("Sản phẩm hiện đang hết hàng");
        }
    }
export const updateUser = (userData) => async (dispatch, getState) => {
    dispatch({ type: "USER_UPDATE_REQUEST" });

    try {

        const auth = localStorage.getItem("auth");
        const token = JSON.parse(auth)?.jwtToken;

        const res = await api.put('/auth/user/update', userData, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        dispatch({
            type: "USER_UPDATE_SUCCESS",
            payload: res.data,
        });
    } catch (error) {
        dispatch({
            type: "USER_UPDATE_FAIL",
            payload: error.response?.data?.message || error.message,
        });
    }
};

export const fetchOrders = () => async (dispatch) => {
    dispatch({ type: "ORDER_FETCH_REQUEST" });

    try {
        const auth = JSON.parse(localStorage.getItem("auth"));
        const token = auth?.jwtToken;

        const res = await api.get("/user/order", {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        dispatch({
            type: "ORDER_FETCH_SUCCESS",
            payload: res.data, // chính là mảng order[] bạn đã gửi ở trên
        });
    } catch (error) {
        dispatch({
            type: "ORDER_FETCH_FAIL",
            payload: error.response?.data?.message || error.message,
        });
    }
};


export const fetchUserOrders = (
    page = 0,
    size = 2,
    sortBy = "orderId",
    sortDir = "desc"
) => async (dispatch) => {
    dispatch({ type: "ORDER_USER_REQUEST" });

    try {
        const auth = JSON.parse(localStorage.getItem("auth"));
        const token = auth?.jwtToken; // phải đồng bộ tên field với backend

        const res = await api.get(
            `/user/order?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            }
        );

        dispatch({
            type: "ORDER_USER_SUCCESS",
            payload: {
                orders: res.data.content,
                totalPages: res.data.totalPages,
                totalElements: res.data.totalElements,
                pageNumber: res.data.pageable?.pageNumber,
                pageSize: res.data.pageable?.pageSize,
                lastPage: res.data.last,
            },
        });
    } catch (error) {
        dispatch({
            type: "ORDER_USER_FAILURE",
            payload: error.response?.data?.message || error.message,
        });
    }
};

export const fetchAdminOrders = (queryString) => async (dispatch) => {
    dispatch({ type: "ORDER_FETCH_REQUEST" });

    try {
        const auth = JSON.parse(localStorage.getItem("auth"));
        const token = auth?.jwtToken;

        const res = await api.get(`/admin/orders?${queryString}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        dispatch({
            type: "ORDER_FETCH_SUCCESS",
            payload: {
                orders: res.data.content,
                pageNumber: res.data.pageNumber,
                pageSize: res.data.pageSize,
                totalElements: res.data.totalElements,
                totalPages: res.data.totalPages,
                lastPage: res.data.lastPage,
            },
        });
    } catch (error) {
        dispatch({
            type: "ORDER_FETCH_FAIL",
            payload: error.response?.data?.message || error.message,
        });
    }
};

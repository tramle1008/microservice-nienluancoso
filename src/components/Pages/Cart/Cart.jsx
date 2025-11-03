import { Button } from "@headlessui/react";
import { FaShoppingCart } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import ItemContent from "./ItemContent";
import { useEffect, useState } from "react";

import { Link } from "react-router-dom";
import { fetchCart } from "../../../store/actions";

const Cart = () => {
    const dispatch = useDispatch();
    const { loading, products, totalPrice, error } = useSelector((state) => state.cart);

    useEffect(() => {
        dispatch(fetchCart());
    }, [dispatch]);

    // console.log("Cart Products:", products);
    // console.log("Type of products:", typeof products);

    return (
        <div className="lg:px-14 sm:px-8 py-10">
            <div>
                <div className="flex flex-col items-center mb-12">
                    <h1 className="text-4xl font-bold text-gray-500 flex items-center gap-1.5">
                        Giỏ Hàng
                    </h1>
                </div>

                <div className="grid grid-cols-6 font-semibold text-center py-2 border-b text-sm md:text-base">
                    <div className="col-span-2 text-left pl-2">Sản phẩm</div>
                    <div>Giá</div>
                    <div>Số lượng</div>
                    <div>Thành tiền</div>
                    <div>Xóa</div>
                </div>

                {loading ? (
                    <p className="text-center mt-4">Đang tải...</p>
                ) : error ? (
                    <p className="text-red-500 mt-4 text-center">{error}</p>
                ) : products.length > 0 ? (
                    <>
                        {products.map((item, index) => (
                            <ItemContent
                                key={index}
                                {...item}
                                onUpdate={() => dispatch(fetchCart())}
                                onRemove={() => dispatch(fetchCart())}
                            />
                        ))}
                        <div className="mt-6 text-xl font-semibold text-right">
                            Tổng cộng: {Number(totalPrice).toLocaleString()} VND
                        </div>
                    </>
                ) : (
                    <p className="text-gray-400 mt-4 text-center">Giỏ hàng trống.</p>
                )}
            </div>

            <div className="flex justify-end mt-4">

                <Link to="/checkout">
                    <button
                        disabled={products.length === 0}
                        className={`${products.length === 0
                            ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                            : "bg-emerald-600 hover:bg-emerald-700 text-white"
                            } font-semibold px-6 py-2 rounded-lg shadow-md transition duration-300`}
                    >
                        Thanh toán
                    </button>
                </Link>

            </div>
        </div>
    );


}

export default Cart;
import { useEffect, useState } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import api from "../../../api/api";

const SetQuantity = ({ productId, quantity, onUpdate, onQuantityChange }) => {
    const [currentQty, setCurrentQty] = useState(quantity);

    useEffect(() => {
        setCurrentQty(quantity); // Cập nhật khi prop quantity thay đổi
    }, [quantity]);

    const updateQuantity = async (operation) => {
        const auth = JSON.parse(localStorage.getItem("auth"));
        if (!auth) {
            toast.error("Bạn cần đăng nhập!");
            return;
        }

        try {
            const token = auth.jwtToken;
            const res = await api.put(
                `/auth/user/cart/products/${productId}/quantity/${operation}`,
                {},
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            const updatedQty = res.data.products.find(p => p.productId === productId)?.quantity;
            if (updatedQty === 0) {
                toast.success("Sản phẩm đã bị xóa khỏi giỏ hàng");
            } else {
                setCurrentQty(updatedQty);
                toast.success(`Đã ${operation === "add" ? "tăng" : "giảm"} số lượng`);

                onQuantityChange?.(updatedQty);
            }

            onUpdate?.();

        } catch (err) {
            console.error("Lỗi cập nhật số lượng:", err);
            toast.error("Không thể cập nhật số lượng!");
        }
    };

    const handleQtyIncrease = () => updateQuantity("add");
    const handleQtyDecrease = () => updateQuantity("delete");

    return (
        <div className="flex items-center gap-4">
            <div className="flex items-center gap-2  ">
                <button
                    onClick={handleQtyDecrease}
                    className="px-3 py-1 border rounded-md disabled:opacity-50"
                    disabled={currentQty <= 1}
                >
                    -
                </button>
                <span className="font-medium">{currentQty}</span>
                <button
                    onClick={handleQtyIncrease}
                    className="px-3 py-1 border rounded-md"
                >
                    +
                </button>
            </div>
        </div>
    );
};

export default SetQuantity;

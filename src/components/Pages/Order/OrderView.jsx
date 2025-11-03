
import { useDispatch, useSelector } from "react-redux";
import { useEffect, useState } from "react";
import { fetchUserOrders } from "../../../store/actions";
import axios from "axios";
import api from "../../../api/api";
import toast from "react-hot-toast";


const OrderView = () => {
    const dispatch = useDispatch();
    const {
        orders = [],
        totalPages = 0,
        loading = false,
        error = null
    } = useSelector((state) => state.orderUser || {});

    const [page, setPage] = useState(0);
    const size = 2;

    useEffect(() => {
        dispatch(fetchUserOrders(page, size));
    }, [dispatch, page]);

    const handleCancel = async (orderId, onSuccess) => {
        try {
            const auth = JSON.parse(localStorage.getItem("auth"));
            const token = auth?.jwtToken;

            const response = await api.put(
                `/orders/${orderId}/reject`,
                {}, // body rỗng
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            toast.success("Đơn hàng đã bị từ chối");
            console.log("Hủy đơn:", orderId);

            if (onSuccess) onSuccess();
        } catch (error) {
            console.error("Lỗi xác nhận từ chối", error);
            toast.error("Không thể từ chối đơn hàng!");
        }
    };

    const handlePrev = () => {
        if (page > 0) setPage((prev) => prev - 1);
    };

    const handleNext = () => {
        if (page < totalPages - 1) setPage((prev) => prev + 1);
    };

    return (
        <div className="max-w-2xl mx-auto mt-5 shadow-md rounded-lg p-4">
            <h2 className="text-xl font-bold mb-4">Lịch sử đơn hàng</h2>

            {loading && <p className="text-gray-600">Đang tải đơn hàng...</p>}

            {error && <p className="text-red-500">{error}</p>}


            {!loading && !error && (
                <>
                    {orders.length === 0 ? (
                        <p>Không có đơn hàng nào.</p>
                    ) : (
                        <>
                            <ul className="space-y-4 border rounded shadow-sm">
                                {orders.map((order) => (
                                    <li key={order.orderId} className="p-2 border-b">
                                        <h3 className="font-semibold">Mã đơn: {order.orderId}</h3>
                                        <p>
                                            <strong>Ngày đặt:</strong>{" "}
                                            {new Date(order.dateOrder).toLocaleDateString("vi-VN")}
                                        </p>
                                        <p>
                                            <strong>Trạng thái thanh toán:</strong>{" "}
                                            {order.paymentStatus}
                                        </p>
                                        <p>
                                            <strong>Trạng thái giao hàng:</strong>{" "}
                                            <span className="text-red-600"> {order.deliveryStatus === "PENDING" ? "Đang duyệt" :
                                                order.deliveryStatus === "SHIPPED" ? "Đang giao hàng" :
                                                    order.deliveryStatus === "REJECTED" ? "Hủy" :
                                                        order.deliveryStatus === "DELIVERED" ? "Đã giao" : ""}
                                            </span></p>
                                        <p>
                                            <strong>Tổng tiền:</strong>{" "}
                                            {order.totalAmount.toLocaleString("vi-VN")} VNĐ
                                        </p>
                                        <p className="mt-2">
                                            <strong>Địa chỉ giao hàng:</strong>{" "}
                                            {order.address?.detail ? `${order.address.detail}, ` : ""}
                                            {order.address?.ward}, {order.address?.district},{" "}
                                            {order.address?.province}
                                        </p>
                                        <p>
                                            <strong>SĐT:</strong> {order.address?.phoneNumber}
                                        </p>


                                        <div className="mt-3">
                                            <h4 className="font-medium">Sản phẩm:</h4>
                                            <ul className="ml-4 list-decimal">
                                                {order.orderItemList.map((item) => (
                                                    <li key={item.orderItemId}>
                                                        {item.product.productName} - SL: {item.quantity} - Giá:{" "}
                                                        {item.orderProductPrice.toLocaleString("vi-VN")} VNĐ
                                                    </li>
                                                ))}
                                            </ul>
                                        </div>

                                        {order.deliveryStatus === "PENDING" && (
                                            <div className="mt-4 flex flex-col gap-2">
                                                <span className="text-sm text-neutral-500">
                                                    Đơn hàng thường được giao sau 4 ngày. <br />
                                                    Chỉ có thể hủy đơn hàng khi trạng thái là PENDING.
                                                </span>
                                                <button
                                                    onClick={() => handleCancel(order.orderId, () => dispatch(fetchUserOrders(page, size)))}
                                                    className="text-rose-700 border border-rose-600 rounded-md bg-red-100 hover:bg-rose-600 hover:text-white transition-colors duration-200 px-6 py-2 font-semibold"
                                                >
                                                    Hủy Đơn Hàng
                                                </button>
                                            </div>
                                        )}
                                    </li>
                                ))}
                            </ul>

                            {/* Phân trang */}
                            <div className="mt-4 flex justify-between items-center">
                                <button
                                    onClick={handlePrev}
                                    disabled={page === 0}
                                    className="px-4 py-2 rounded border bg-gray-100 hover:bg-gray-200 disabled:opacity-50"
                                >
                                    Trang trước
                                </button>

                                <span className="text-sm text-gray-600">
                                    Trang {page + 1} / {totalPages}
                                </span>

                                <button
                                    onClick={handleNext}
                                    disabled={page >= totalPages - 1}
                                    className="px-4 py-2 rounded border bg-gray-100 hover:bg-gray-200 disabled:opacity-50"
                                >
                                    Trang sau
                                </button>
                            </div>
                        </>
                    )}
                </>
            )}
        </div>
    );
};

export default OrderView;

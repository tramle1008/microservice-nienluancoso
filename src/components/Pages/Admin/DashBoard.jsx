import { useEffect, useState } from "react";
import axios from "axios";
import { useDispatch, useSelector } from "react-redux";
import { fetchProduct } from "../../../store/actions";
import PaginationRounded from "../../PaginationRounded";
import { Link } from "react-router-dom";
import api from "../../../api/api";

const Dashboard = () => {
    const [stats, setStats] = useState(null);
    const { products } = useSelector((state) => state.products);
    const dispatch = useDispatch();

    useEffect(() => {
        const token = JSON.parse(localStorage.getItem("auth"))?.jwtToken;

        api.get("/admin/stats", {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then(res => setStats(res.data))
            .catch(err => console.error("Lỗi lấy thống kê:", err));
        dispatch(fetchProduct());
    }, [dispatch]);

    if (!stats) {
        return <p>Đang tải dữ liệu...</p>;
    }

    // Lọc sản phẩm sắp hết hàng (tồn kho < 5)
    const lowStockProducts = products?.filter(p => p.quantity < 5) || [];

    return (
        <div style={{ padding: "20px" }}>
            <h1>Dashboard Admin</h1>
            <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
                gap: '20px',
                marginTop: '45px'
            }}>
                <StatCard label="Khách hàng" value={stats.totalUsers} />
                <Link to="/admin/product">
                    <StatCard label="Sản phẩm" value={stats.totalProducts} />
                </Link>
                <Link to="/admin/orders">
                    <StatCard label="Đơn chờ xác nhận" value={stats.pendingDeliveries} />
                </Link>
                <StatCard label="Doanh thu" value={stats.totalRevenue.toLocaleString('vi-VN') + ' ₫'} />
            </div>

            <h1 className="text-2xl font-semibold text-slate-800 text-center my-5">Sản phẩm sắp hết hàng</h1>
            <div className="rounded shadow bg-white">
                <table className="min-w-full table-auto border-collapse border border-slate-300 mr-18">
                    <thead className="bg-slate-100">
                        <tr>
                            <th className="border px-4 py-2">#id</th>
                            <th className="border px-4 py-2">Ảnh</th>
                            <th className="border px-4 py-2">Tên</th>
                            <th className="border px-4 py-2">Mô tả</th>
                            <th className="border px-4 py-2">Giá</th>
                            <th className="border px-4 py-2 text-red-600">Tồn kho</th>
                        </tr>
                    </thead>
                    <tbody>
                        {lowStockProducts.length > 0 ? (
                            lowStockProducts.map((product, index) => (
                                <tr key={index} className="hover:bg-slate-50">
                                    <td className="border px-4 py-2 text-center">{product.productId}</td>
                                    <td className="border px-4 py-2 text-center">
                                        {product.image ? (
                                            <img
                                                src={product.image}
                                                alt={product.productName}
                                                className="mt-1 w-14 h-14 object-cover rounded-md mx-auto"
                                            />
                                        ) : (
                                            <span className="text-gray-400 italic">Không có ảnh</span>
                                        )}
                                    </td>
                                    <td className="border px-4 py-2">{product.productName}</td>
                                    <td className="border px-4 py-2">{product.description}</td>
                                    <td className="border px-4 py-2 text-right">
                                        {product.price?.toLocaleString()} VND
                                    </td>
                                    <td className="border px-4 py-2 text-center">{product.quantity}</td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="6" className="text-center text-slate-500 py-4">
                                    Không có sản phẩm nào sắp hết hàng.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>


        </div>
    );
};

const StatCard = ({ label, value }) => (
    <div style={{
        background: "#f9f9f9",
        padding: "20px",
        borderRadius: "12px",
        boxShadow: "0 0 10px rgba(0,0,0,0.05)",
        textAlign: "center"
    }}>
        <h2 style={{ fontSize: "24px", marginBottom: "10px" }}>{value}</h2>
        <p style={{ fontSize: "16px", color: "#555" }}>{label}</p>
    </div>
);

export default Dashboard;

import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchCategories, fetchProduct } from "../../../store/actions";
import { Link, useSearchParams } from "react-router-dom";

import PaginationRounded from "../../PaginationRounded";
import AdminSidebar from "./AdminSidebar";
import { MdDelete, MdDeleteOutline } from "react-icons/md";
import { FaEdit } from "react-icons/fa";

const AdminProductList = () => {
    const { products, pagination } = useSelector((state) => state.products);
    const dispatch = useDispatch();
    const [searchParams] = useSearchParams();
    const page = Number(searchParams.get("page")) || 1;

    useEffect(() => {
        const pageIndex = page - 1;
        dispatch(fetchProduct(`pageNumber=${pageIndex}&pageSize=5&sortBy=productId&sortOrder=asc`));
    }, [dispatch, page]);


    return (
        <div className="flex min-h-screen bg-gray-100">

            <AdminSidebar />
            <div className="flex-1 p-6 mt-[50px]">
                <div className="flex items-center justify-between mb-4 mt-2">
                    <h1 className="text-2xl font-semibold text-slate-800">Danh sách sản phẩm</h1>
                    <div >
                        <a
                            href="/admin/product/addproduct"
                            target="_blank"
                            rel="noopener noreferrer" //Ngăn không cho trang mới điều khiển lại tab cũ Không gửi địa chỉ trang gốc của bạn cho trang mới
                            className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md mx-2"
                        >
                            Thêm sản phẩm
                        </a>


                    </div>

                </div>

                <div className=" rounded shadow bg-white">
                    <table className="min-w-full table-auto border-collapse border border-slate-300">
                        <thead className="bg-slate-100">
                            <tr>
                                <th className="border px-4 py-2">#id</th>
                                <th className="border px-4 py-2">Ảnh</th>
                                <th className="border px-4 py-2">Tên</th>
                                <th className="border px-4 py-2">Mô tả</th>
                                <th className="border px-4 py-2">Giá</th>
                                <th className="border px-4 py-2">Giảm (%)</th>
                                <th className="border px-4 py-2">Tồn kho</th>
                                <th className="border px-4 py-2">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            {products && products.length > 0 ? (
                                products.map((product, index) => (
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
                                            {product.price?.toLocaleString()} ₫
                                        </td>
                                        <td className="border px-4 py-2 text-center">{product.discount}%</td>
                                        <td className="border px-4 py-2 text-center">{product.quantity}</td>
                                        <td className="border px-4 py-2 text-center align-middle">
                                            <div className="flex items-center justify-center gap-2">
                                                <a
                                                    href={`/admin/product/update/${product.productId}`}
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    className="text-gray-500 hover:text-gray-800"
                                                >
                                                    <FaEdit size={25} />
                                                </a>
                                                <a
                                                    href="#"
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    className="text-red-500 hover:text-red-700"
                                                >
                                                    <MdDelete size={25} />
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="7" className="text-center text-slate-500 py-4">
                                        Không có sản phẩm nào.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>

                {pagination && (
                    <div className="mt-4 text-sm text-slate-600 text-center">
                        Trang {pagination.pageNumber + 1} / {pagination.totalPages} — Tổng cộng:{" "}
                        {pagination.totalElements} sản phẩm
                    </div>
                )}

                <PaginationRounded
                    numberofPage={pagination?.totalPages}
                    totalProducts={pagination?.totalElements}
                />
            </div>
        </div >
    );
};

export default AdminProductList;

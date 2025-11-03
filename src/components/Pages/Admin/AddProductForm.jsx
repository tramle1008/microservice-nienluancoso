import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";

import axios from "axios";
import { toast } from "react-hot-toast";
import { useDispatch, useSelector } from "react-redux"; // ✅ lấy danh mục từ store
import InputField from "../../InputField";
import { fetchCategories } from "../../../store/actions";
import AdminSidebar from "./AdminSidebar";
import api from "../../../api/api";

const AddProductForm = () => {
    const {
        register,
        handleSubmit,
        formState: { errors },
        reset,
    } = useForm();
    const dispatch = useDispatch();

    const categories = useSelector((state) => state.products.categories);

    useEffect(() => {
        dispatch(fetchCategories());
    }, [dispatch]);

    const [selectedCategoryId, setSelectedCategoryId] = useState(
        categories?.[0]?.categoryID || ""
    );
    const [imageFile, setImageFile] = useState(null);

    const onSubmit = async (data) => {
        try {
            const authData = localStorage.getItem("auth");
            if (!authData) {
                toast.error("Vui lòng đăng nhập!");
                return;
            }

            const { jwtToken } = JSON.parse(authData);
            if (!jwtToken) {
                toast.error("Phiên đăng nhập hết hạn!");
                return;
            }


            const response = await api.post(
                `/admin/categories/${selectedCategoryId}/product/default`,
                {
                    productName: data.productName,
                    description: data.description,
                    quantity: parseInt(data.quantity),
                    price: parseInt(data.price),
                    discount: parseInt(data.discount),
                },
                {
                    headers: {
                        Authorization: `Bearer ${jwtToken}`,
                        "Content-Type": "application/json",
                    },
                }
            );

            const createdProduct = response.data;
            toast.success("Đã thêm sản phẩm. Đang tải ảnh...");

            if (imageFile) {
                const formData = new FormData();
                formData.append("image", imageFile);

                await api.put(
                    `/products/${createdProduct.productId}/image`,
                    formData,
                    {
                        headers: {
                            Authorization: `Bearer ${jwtToken}`,
                            "Content-Type": "multipart/form-data",
                        },
                    }
                );

                toast.success("Upload ảnh thành công!");
            }

            reset();
            setImageFile(null);
        } catch (err) {
            const msg = err?.response?.data?.message || "Lỗi thêm sản phẩm";
            toast.error(msg);
            console.error(err);
        }
    };

    return (
        <div>
            <AdminSidebar />
            <div className="max-w-xl mx-auto bg-white p-6 rounded-lg shadow mt-12">
                <h2 className="text-xl font-semibold mb-4 text-slate-800">
                    Thêm Sản Phẩm
                </h2>

                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">

                    <div className="flex flex-col gap-1">
                        <label className="font-bold text-sm text-slate-800">Danh mục</label>
                        <select
                            value={selectedCategoryId}
                            onChange={(e) => setSelectedCategoryId(e.target.value)}
                            className="border border-slate-700 rounded p-2"
                        >
                            {categories.map((cat) => (
                                <option key={cat.categoryID} value={cat.categoryID}>
                                    {cat.categoryName}
                                </option>
                            ))}
                        </select>
                    </div>

                    {/* Các input */}
                    <InputField
                        id="productName"
                        label="Tên sản phẩm"
                        type="text"
                        placeholder="VD: Áo hoodie"
                        required
                        message="Không được để trống"
                        register={register}
                        errors={errors}
                    />
                    <InputField
                        id="description"
                        label="Mô tả"
                        type="text"
                        placeholder="VD: Chất nỉ bông"
                        required
                        message="Không được để trống"
                        register={register}
                        errors={errors}
                    />
                    <InputField
                        id="quantity"
                        label="Số lượng"
                        type="number"
                        placeholder="VD: 50"
                        required
                        message="Không được để trống"
                        register={register}
                        errors={errors}
                    />
                    <InputField
                        id="price"
                        label="Giá"
                        type="number"
                        placeholder="VD: 350000"
                        required
                        message="Không được để trống"
                        register={register}
                        errors={errors}
                    />
                    <InputField
                        id="discount"
                        label="Giảm giá (%)"
                        type="number"
                        placeholder="VD: 10"
                        required
                        message="Không được để trống"
                        register={register}
                        min={0}
                        errors={errors}
                        default={0}
                    />

                    {/* Upload ảnh */}
                    <div className="flex flex-col gap-1">
                        <label className="font-bold text-sm text-slate-800">Ảnh sản phẩm</label>
                        <input
                            type="file"
                            accept="image/*"
                            onChange={(e) => setImageFile(e.target.files[0])}
                            className="border border-slate-700 p-2 rounded"
                        />
                    </div>

                    <button
                        type="submit"

                        className="w-full bg-[#1F2937] hover:bg-blue-200 text-white py-2 rounded-md"
                    >
                        Thêm Sản Phẩm
                    </button>
                </form>
            </div>
        </div>
    );
};

export default AddProductForm;

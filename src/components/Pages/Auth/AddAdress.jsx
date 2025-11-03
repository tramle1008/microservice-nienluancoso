import { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import toast from "react-hot-toast";
import axios from "axios";
import { fetchAddresses } from "../../../store/actions";
import InputField from "../../InputField";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import api from "../../../api/api";

const AddAdress = () => {
    const dispatch = useDispatch();
    const { register, handleSubmit, formState: { errors } } = useForm();
    const storedAuth = localStorage.getItem("auth");
    const { addresses } = useSelector((state) => state.address);
    const [auth, setAuth] = useState(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    useEffect(() => {
        dispatch(fetchAddresses());
    }, [dispatch]);

    const handleAddAdress = async (data) => {
        let token;
        try {
            const parsedAuth = JSON.parse(storedAuth);
            setAuth(parsedAuth);
            token = parsedAuth?.jwtToken;
        } catch {
            setError("Token không hợp lệ");
            return;
        }

        if (!token) {
            toast.error("Phiên đăng nhập của bạn đã hết hạn");
            navigate("/login");
            return;
        }

        try {
            await api.post("/auth/user/addresses", {
                province: data.province,
                district: data.district,
                ward: data.ward,
                detail: data.detail,
                phoneNumber: data.phoneNumber,
            }, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            toast.success("Thêm địa chỉ thành công!");
            dispatch(fetchAddresses());
            navigate(-1);
        } catch (error) {
            toast.error("Thêm địa chỉ thất bại");
            console.error("Lỗi thêm địa chỉ:", error);
        }
    };

    return (
        <div className="bg-white p-6 rounded shadow-md max-w-md mx-auto my-10">
            <h2 className="text-2xl font-bold mb-4  text-center">Thêm địa chỉ</h2>
            <form onSubmit={handleSubmit(handleAddAdress)} className="flex flex-col gap-4">
                <InputField
                    label="Tỉnh/Thành phố"
                    id="province"
                    type="text"
                    placeholder="Cần Thơ"
                    register={register}
                    errors={errors}
                    required
                    message="Không được để trống"
                />
                <InputField
                    label="Quận/Huyện"
                    id="district"
                    type="text"
                    placeholder="Ninh Kiều"
                    register={register}
                    errors={errors}
                    required
                    message="Không được để trống"
                />
                <InputField
                    label="Phường/Xã"
                    id="ward"
                    type="text"
                    placeholder="An Khánh"
                    register={register}
                    errors={errors}
                    required
                    message="Không được để trống"
                />
                <InputField
                    label="Ấp/ Số nhà, tên đường"
                    id="detail"
                    type="text"
                    placeholder="Ấp 6/ A52, đường số 6"
                    register={register}
                    errors={errors}
                    required
                    message="Không được để trống"
                />
                <InputField
                    label="Số điện thoại"
                    id="phoneNumber"
                    type="text"
                    placeholder="0123456789"
                    register={register}
                    errors={errors}
                    required
                    message="Không được để trống"
                />
                <button
                    type="submit"
                    className="bg-gray-700 text-white py-2 px-4 rounded-lg hover:bg-emerald-700 transition duration-300"
                >
                    Thêm
                </button>
            </form>

            {addresses && addresses.length > 0 && (
                <div className="mt-6">
                    <h3 className="font-semibold mb-2">Địa chỉ hiện tại:</h3>
                    <ul className="space-y-1 text-sm text-gray-700">
                        {addresses.map((addr) => (
                            <li key={addr.addressId}>
                                • {addr.detail ? `${addr.detail}, ` : ''} {addr.ward}, {addr.district}, {addr.province}
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
};

export default AddAdress;

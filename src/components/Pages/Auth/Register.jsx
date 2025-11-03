import { useForm } from "react-hook-form";
import axios from "axios";
import { toast } from "react-hot-toast";
import InputField from "../../InputField";
import { useNavigate } from "react-router-dom";
import api from "../../../api/api";

const Register = () => {
    const { register, handleSubmit, formState: { errors } } = useForm();
    const navigate = useNavigate();

    const handleRegister = async (data) => {
        try {
            await api.post("/auth/signup", {
                username: data.username,
                email: data.email,
                password: data.password,
                role: null,
            }, {
                headers: { "Content-Type": "application/json" },
            });

            const loginRes = await api.post("/auth/signin", {
                username: data.username,
                password: data.password,
            });

            const token = loginRes.data?.jwtToken;

            if (!token) {
                toast.error("Không thể lấy token sau khi đăng ký");
                return;
            }

            // 3. Gửi địa chỉ
            await api.post("/auth/user/addresses", {
                province: data.province,
                district: data.district,
                ward: data.ward,
                phoneNumber: data.phoneNumber,
            }, {
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            toast.success("Đăng ký và thêm địa chỉ thành công!");
            navigate("/login");

        } catch (error) {
            const msg = error.response?.data?.message || "Có lỗi xảy ra!";
            toast.error(msg);
            console.error("Lỗi đăng ký:", error);
        }
    };

    return (
        <div className="max-w-md mx-auto mt-10 mb-10 bg-white shadow-lg p-6 rounded-xl border">
            <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">Đăng ký</h2>

            <form onSubmit={handleSubmit(handleRegister)} className="flex flex-col gap-4">
                <InputField
                    label="Tên người dùng"
                    id="username"
                    type="text"
                    placeholder="Nhập tên"
                    register={register}
                    errors={errors}
                    required
                    message="Không được để trống"
                />

                <InputField
                    label="Email"
                    id="email"
                    type="email"
                    placeholder="Nhập email"
                    register={register}
                    errors={errors}
                    required
                    message="Không được để trống"
                />

                <InputField
                    label="Mật khẩu"
                    id="password"
                    type="password"
                    placeholder="Nhập mật khẩu"
                    register={register}
                    errors={errors}
                    required
                    message="Không được để trống"
                    min={8}
                />

                {/* Địa chỉ */}
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
                    className="bg-emerald-500 text-white py-2 px-4 rounded-lg hover:bg-cyan-800 transition duration-300"
                >
                    Đăng ký
                </button>
            </form>
        </div>
    );
};

export default Register;

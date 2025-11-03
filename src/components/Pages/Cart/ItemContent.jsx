import { useState } from "react";
import SetQuantity from "./SetQuantity";
import toast from "react-hot-toast";
import DeleteItem from "./DeleteItem";

const ItemContent = ({
    productId,
    productName,
    image,
    description,
    quantity,
    price,
    discount,
    specialPrice,
    onRemove,
    onUpdate
}) => {
    const [currentQuantity, setCurrentQuantity] = useState(quantity);

    const handleQuantityChange = (newQty) => {
        setCurrentQuantity(newQty);
    };

    const handleRemoveFromCart = () => {
        DeleteItem(productId, () => {
            if (typeof onRemove === "function") {
                onRemove();
            }
        });
    };

    return (
        <div className="grid grid-cols-6 items-center py-4 border-b border-slate-200 text-sm md:text-base">
            <div className="col-span-2 flex items-center gap-4">
                <img
                    src={`${import.meta.env.VITE_BACK_END_URL}/images/${image}`}
                    alt={productName}
                    className="w-26 h-26 object-cover rounded-md"
                />
                <div>
                    <h4 className="font-semibold">{productName}</h4>
                    <p className="text-gray-500 text-xs md:text-sm">{description}</p>
                </div>
            </div>

            <div className="text-center text-emerald-700 font-semibold">
                {Number(specialPrice).toLocaleString()} đ
            </div>

            <div className="flex justify-center">
                <SetQuantity
                    productId={productId}
                    quantity={quantity}
                    onUpdate={onUpdate}
                    onQuantityChange={handleQuantityChange}
                />
            </div>

            <div className="text-center font-semibold text-gray-700">
                {(Number(currentQuantity) * Number(specialPrice)).toLocaleString()} đ
            </div>

            <div className="flex justify-center">
                <button className="flex items-center font-bold px-4 py-1 border border-rose-600 rounded-md hover:bg-red-50 transition-colors duration-200"
                    onClick={() => handleRemoveFromCart()}>
                    Xóa
                </button>
            </div>
        </div>
    );
};

export default ItemContent;

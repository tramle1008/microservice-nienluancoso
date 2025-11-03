import { FaExclamationTriangle } from "react-icons/fa";

import { useDispatch, useSelector } from "react-redux";
import { useEffect } from "react";
import { fetchCategories, fetchProduct } from "../../../store/actions";
import Filter from "../../Filter";
import useProductFilter from "../../useProductFilter";
import PaginationRounded from "../../PaginationRounded";
import Navbar from "../Navbar";
import ReusableFilter from "../../ReusableFilter";
import ProductCard from "./ProductCard";

const Product = () => {
    const dispatch = useDispatch();
    const { isLoading, errorMessage } = useSelector((state) => state.products);
    const { products, pagination } = useSelector((state) => state.products);
    const categories = useSelector((state) => state.products.categories) || [];
    useEffect(() => {
        dispatch(fetchCategories());
    }, [dispatch]);

    const categoryFilterList = categories
        .map((cat) => ({
            value: cat.categoryID,
            label: cat.categoryName,
        }));
    useProductFilter();
    return (
        <section>


            <div className="lg:px-14 sm:px-8 px-2 py-4 2xl:w-[90%] 2xl:mx-auto bg-[#92a695]">

                <ReusableFilter
                    filterLabel="Category"
                    filterParam="categoryId"
                    filterList={categoryFilterList}
                    searchEnabled={true}
                    sortEnabled={true}
                />
                {isLoading ? (
                    <p>is loading ... </p>
                ) : errorMessage ? (
                    <div className='flex justify-center items-center h-[200px]'>
                        <FaExclamationTriangle className="text-slate-800 text-3xl mr-2" />
                        <span className="text-slate-800">{errorMessage}</span>
                    </div>
                ) : (

                    <section className="px-4 sm:px-4 lg:px-14 pt-10 2xl:w-[90%] 2xl:mx-auto">
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
                            {products && products.map((item) => (
                                <ProductCard key={item.productId} {...item} />
                            ))}
                        </div>
                        <div className="flex justify-center px-10 ">
                            <PaginationRounded
                                numberofPage={pagination?.totalPages}
                                totalProducts={pagination?.totalElements}
                            />

                        </div>
                    </section>

                )}
            </div>
        </section>
    );
};

export default Product;

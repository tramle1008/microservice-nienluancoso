import { useSearchParams } from "react-router-dom";
import { useDispatch } from 'react-redux';
import { useEffect } from "react";
import { fetchProduct } from "../store/actions";

const useProductFilter = () => {
    const [searchParams] = useSearchParams();
    const dispatch = useDispatch();
    useEffect(() => {
        const params = new URLSearchParams();

        const currentPage = (searchParams.get("page")) || "1";
        const sortOrder = searchParams.get("sortOrder") || "asc";
        const categoryId = searchParams.get("categoryId");
        const key = searchParams.get("key");

        params.set("pageNumber", (currentPage - 1));
        params.set("sortBy", "productId");
        params.set("sortOrder", sortOrder);

        if (categoryId !== null && categoryId !== "undefined") {
            params.set("categoryId", categoryId);
        }

        if (key) params.set("key", key);

        const queryString = params.toString();

        dispatch(fetchProduct(queryString));
    }, [dispatch, searchParams]);

};

export default useProductFilter;

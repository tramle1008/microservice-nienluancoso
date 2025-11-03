
const initialState = {
    products: [],
    categories: [],
    pagination: {},
};

export const productReducer = (state = initialState, action) => {
    switch (action.type) {
        case "FETCH_PRODUCTS":
            return {
                ...state,
                products: action.payload.products,
                pagination: {
                    pageNumber: action.payload.pageNumber,
                    pageSize: action.payload.pageSize,
                    totalElements: action.payload.totalElements,
                    totalPages: action.payload.totalPages,
                    lastPage: action.payload.lastPage,
                },
            };
        case "FETCH_CATEGORIES":
            return {
                ...state,
                categories: action.payload,
            };

        default:
            return state;
    }
};

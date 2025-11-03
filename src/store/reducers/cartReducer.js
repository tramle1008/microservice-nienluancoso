
const initialState = {
    loading: false,
    products: [],
    totalPrice: 0,
    error: null,
};

export const cartReducer = (state = initialState, action) => {
    switch (action.type) {
        case 'CART_REQUEST':
            return { ...state, loading: true };

        case 'CART_SUCCESS':
            return {
                ...state,
                loading: false,
                products: action.payload.products,
                totalPrice: action.payload.totalPrice,
                error: null,
            };

        case 'CART_FAILURE':
            return {
                ...state,
                loading: false,
                error: action.payload,
            };

        default:
            return state;
    }
};

const initialState = {
    loading: false,
    orders: [],
    error: null,
};

export const orderReducer = (state = initialState, action) => {
    switch (action.type) {
        case "ORDER_FETCH_REQUEST":
            return { ...state, loading: true };

        case "ORDER_FETCH_SUCCESS":
            return { ...state, loading: false, orders: action.payload };

        case "ORDER_FETCH_FAIL":
            return { ...state, loading: false, error: action.payload };

        default:
            return state;
    }
};

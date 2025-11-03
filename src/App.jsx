
import './App.css'

import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom';
import Home from './components/Pages/Home';
import Navbar from './components/Pages/Navbar';
import { Toaster } from 'react-hot-toast';
import Cart from './components/Pages/Cart/Cart';
import Login from './components/Pages/Auth/Login';
import Profile from './components/Pages/Auth/Profile';
import UpdateAccountForm from './components/UpdateAccountForm';
import Register from './components/Pages/Auth/Register';
import Checkout from './components/Pages/Order/Checkout';

import AdminRoute from './components/Pages/Admin/AdminRoute';
import HandleOrder from './components/Pages/Admin/HandleOrder';
import OrderShipped from './components/Pages/Admin/DetailOrder';
import AdminLayout from './components/Pages/Admin/AdminLayout';
import Logout from './components/Pages/Auth/Logout';
import OrderView from './components/Pages/Order/OrderView';
import DeliverRoute from './components/Pages/Delivery/DeliverRoute';
import Delivery from './components/Pages/Delivery/Delivery';
import AddProductForm from './components/Pages/Admin/AddProductForm';
import AdminProductList from './components/Pages/Admin/AdminProductList';
import UpdateProduct from './components/Pages/Admin/UpdateProduct';
import DetailOrder from './components/Pages/Admin/DetailOrder';
import AddAdress from './components/Pages/Auth/AddAdress';
import ChangePasswd from './components/Pages/Auth/ChangePasswd';
import WebFooter from './components/Pages/WebFooter';
import Product from './components/Pages/Product/Product';

function AppContent() {
  const location = useLocation();
  const isAdminPage = location.pathname.startsWith('/admin');
  return (
    <>
      <Toaster position="top-center" />
      {!isAdminPage && <Navbar />}
      <Routes>

        <Route path="/" element={<Home />} />
        <Route path="/products" element={<Product />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/user/profile" element={<Profile />} />
        <Route path="/logout" element={<Logout />} />
        <Route path="/user/order" element={<OrderView />} />
        <Route path="/checkout" element={<Checkout />} />
        <Route path="/user/update/address" element={<AddAdress />} />
        <Route path="/user/update/password" element={<ChangePasswd />} />
        <Route path="/admin" element={<AdminRoute> <AdminLayout /></AdminRoute>} />
        <Route path="/admin/orders" element={<HandleOrder />} />
        <Route path="/admin/orders/ships" element={<OrderShipped />} />
        <Route path='/admin/product' element={<AdminProductList />} />
        <Route path='/admin/detail' element={<DetailOrder />} />
        <Route path="/admin/product/addproduct" element={<AddProductForm />} />
        <Route path="/deliver" element={<DeliverRoute> <Delivery /></DeliverRoute>} />
        <Route path="/admin/product/update/:productId" element={<UpdateProduct />} />

      </Routes>
      {!isAdminPage && <WebFooter />}

    </>
  );
}


function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}

export default App;
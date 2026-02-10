import axios from "../axios";
import { useState, useEffect, createContext, useCallback } from "react";

const AppContext = createContext({
  data: [],
  isError: "",
  cart: [],
  totalPages: 0,
  pageNumber: 0,
  pageSize: 8,
  addToCart: (product) => { },
  removeFromCart: (productId) => { },
  refreshData: (page, size) => { },
  clearCart: () => { },
});

export const AppProvider = ({ children }) => {
  const [data, setData] = useState([]);
  const [isError, setIsError] = useState("");
  const [cart, setCart] = useState(JSON.parse(localStorage.getItem("cart")) || []);

  // Pagination state
  const [totalPages, setTotalPages] = useState(0);
  const [pageNumber, setPageNumber] = useState(0);
  const [pageSize, setPageSize] = useState(8);

  const addToCart = (product) => {
    const existingProductIndex = cart.findIndex((item) => item.id === product.id);
    if (existingProductIndex !== -1) {
      const updatedCart = cart.map((item, index) =>
        index === existingProductIndex ? { ...item, quantity: item.quantity + 1 } : item
      );
      setCart(updatedCart);
      localStorage.setItem("cart", JSON.stringify(updatedCart));
    } else {
      const updatedCart = [...cart, { ...product, quantity: 1 }];
      setCart(updatedCart);
      localStorage.setItem("cart", JSON.stringify(updatedCart));
    }
  };

  const removeFromCart = (productId) => {
    const updatedCart = cart.filter((item) => item.id !== productId);
    setCart(updatedCart);
    localStorage.setItem("cart", JSON.stringify(updatedCart));
  };

  // ✅ Pagination-aware refreshData
  const refreshData = useCallback(async (page = 0, size = 8) => {
    try {
      setIsError("");

      // IMPORTANT:
      // Your backend must return Page<Product> for /products endpoint:
      // { content: [...], totalPages: X, number: page, size: size, ... }
      const response = await axios.get(`/products?page=${page}&size=${size}`);

      // If backend returns Page<Product>
      setData(response.data.content);
      setTotalPages(response.data.totalPages);
      setPageNumber(response.data.number);
      setPageSize(response.data.size);
    } catch (error) {
      setIsError(error.message);
      setData([]);
      setTotalPages(0);
    }
  }, []);

  const clearCart = () => {
    setCart([]);
    localStorage.setItem("cart", JSON.stringify([]));
  };

  // First load (page 0)
  useEffect(() => {
    refreshData(0, 8);
  }, [refreshData]);

  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cart));
  }, [cart]);

  return (
    <AppContext.Provider
      value={{
        data,
        isError,
        cart,
        totalPages,
        pageNumber,
        pageSize,
        addToCart,
        removeFromCart,
        refreshData,
        clearCart,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export default AppContext;

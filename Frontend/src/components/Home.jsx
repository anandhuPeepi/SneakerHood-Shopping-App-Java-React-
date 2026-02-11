import React, { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import AppContext from "../Context/Context";
import unplugged from "../assets/unplugged.png";

const Home = ({ selectedCategory }) => {
  const { data, isError, addToCart, refreshData, totalPages } =
    useContext(AppContext);

  const [products, setProducts] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(8);

  // ✅ RESET page when category changes (THIS is what you asked)
  useEffect(() => {
    setPage(0);
  }, [selectedCategory]);

  // ✅ Fetch products whenever page OR category changes
  useEffect(() => {
    refreshData(page, size, selectedCategory); // ✅ pass category to backend
  }, [refreshData, page, size, selectedCategory]);

  // ✅ Fetch images for each product
  useEffect(() => {
    if (data && data.length > 0) {
      const fetchImagesAndUpdateProducts = async () => {
        const updatedProducts = await Promise.all(
          data.map(async (product) => {
            try {
              const response = await axios.get(
                `http://localhost:8080/api/product/${product.id}/image`,
                { responseType: "blob", validateStatus: () => true }
              );

              if (response.status === 200) {
                const imageUrl = URL.createObjectURL(response.data);
                return { ...product, imageUrl };
              }

              return { ...product, imageUrl: "placeholder-image-url" };
            } catch (error) {
              return { ...product, imageUrl: "placeholder-image-url" };
            }
          })
        );
        setProducts(updatedProducts);
      };

      fetchImagesAndUpdateProducts();
    } else {
      setProducts([]);
    }
  }, [data]);

  // ✅ No need to filter on frontend anymore (backend already filters)
  const finalProducts = products;

  if (isError) {
    return (
      <h2 className="text-center" style={{ padding: "18rem" }}>
        <img
          src={unplugged}
          alt="Error"
          style={{ width: "100px", height: "100px" }}
        />
      </h2>
    );
  }

  return (
    <>
      <div
        className="grid"
        style={{
          marginTop: "64px",
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
          gap: "20px",
          padding: "20px",
        }}
      >
        {finalProducts.length === 0 ? (
          <h2
            className="text-center"
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            No Products Available
          </h2>
        ) : (
          finalProducts.map((product) => {
            const { id, brand, name, price, productAvailable, imageUrl } =
              product;

            return (
              <div
                className="card mb-3"
                style={{
                  width: "250px",
                  height: "360px",
                  boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
                  borderRadius: "10px",
                  overflow: "hidden",
                  backgroundColor: productAvailable ? "#fff" : "#ccc",
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "flex-start",
                  alignItems: "stretch",
                }}
                key={id}
              >
                <Link
                  to={`/product/${id}`}
                  style={{ textDecoration: "none", color: "inherit" }}
                >
                  <img
                    src={imageUrl}
                    alt={name}
                    style={{
                      width: "100%",
                      height: "150px",
                      objectFit: "cover",
                      padding: "5px",
                      margin: "0",
                      borderRadius: "10px",
                    }}
                  />
                  <div
                    className="card-body"
                    style={{
                      flexGrow: 1,
                      display: "flex",
                      flexDirection: "column",
                      justifyContent: "space-between",
                      padding: "10px",
                    }}
                  >
                    <div>
                      <h5
                        className="card-title"
                        style={{
                          margin: "0 0 10px 0",
                          fontSize: "1.2rem",
                        }}
                      >
                        {name.toUpperCase()}
                      </h5>
                      <i
                        className="card-brand"
                        style={{ fontStyle: "italic", fontSize: "0.8rem" }}
                      >
                        {"~ " + brand}
                      </i>
                    </div>

                    <hr className="hr-line" style={{ margin: "10px 0" }} />

                    <div className="home-cart-price">
                      <h5
                        className="card-text"
                        style={{
                          fontWeight: "600",
                          fontSize: "1.1rem",
                          marginBottom: "5px",
                        }}
                      >
                        € {price}
                      </h5>
                    </div>

                    <button
                      className="btn-hover color-9"
                      style={{ margin: "10px 25px 0px " }}
                      onClick={(e) => {
                        e.preventDefault();
                        addToCart(product);
                      }}
                      disabled={!productAvailable}
                    >
                      {productAvailable ? "Add to Cart" : "Out of Stock"}
                    </button>
                  </div>
                </Link>
              </div>
            );
          })
        )}
      </div>

      {/* ✅ Pagination Buttons */}
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          gap: "12px",
          paddingBottom: "30px",
        }}
      >
        <button
          className="btn btn-secondary"
          onClick={() => setPage((p) => Math.max(p - 1, 0))}
          disabled={page === 0}
        >
          Prev
        </button>

        <span style={{ paddingTop: "8px" }}>
          Page {page + 1} {totalPages ? `of ${totalPages}` : ""}
        </span>

        <button
          className="btn btn-secondary"
          onClick={() => setPage((p) => p + 1)}
          disabled={totalPages ? page >= totalPages - 1 : false}
        >
          Next
        </button>
      </div>
    </>
  );
};

export default Home;

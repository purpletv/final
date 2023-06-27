package eStoreProduct.DAO.customer;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eStoreProduct.DAO.admin.ProdStockDAO;
import eStoreProduct.model.admin.entities.productsModel;
import eStoreProduct.model.customer.entities.cartModel;
import eStoreProduct.model.customer.entities.custCredModel;
import eStoreProduct.model.customer.output.ServiceableRegion;
import eStoreProduct.utility.ProductStockPrice;

@Component
public class cartDAOImp implements cartDAO {

	@PersistenceContext
	private EntityManager entityManager;

	private ProdStockDAO prodStockDAO;

	@Autowired
	public cartDAOImp(ProdStockDAO prodStockDAO) {
		this.prodStockDAO = prodStockDAO;
	}

	@Transactional
	public String addToCart(int productId, int customerId) {
		String selectQuery = "SELECT COUNT(c) FROM cartModel c WHERE c.customer.custId = :customerId AND c.product.id = :productId";

		Query countQuery = entityManager.createQuery(selectQuery).setParameter("customerId", customerId)
				.setParameter("productId", productId);

		Long count = (Long) countQuery.getSingleResult();
		System.out.println("in addToCart customerId:" + customerId);
		System.out.println("in addToCart productId:" + productId);
		if (count == 0) {
			cartModel cartItem = new cartModel();
			cartItem.setCustomer(entityManager.find(custCredModel.class, customerId));
			cartItem.setProduct(entityManager.find(productsModel.class, productId));
			cartItem.setQuantity(1);
			entityManager.persist(cartItem);
			System.out.println("inserted into cart");
			return "Added to cart";
		} else {

			return "Already added to cart";
		}
	}

	@Transactional
	public int removeFromCart(int productId, int customerId) {
		String deleteQuery = "DELETE FROM cartModel c WHERE c.customer.custId = :customerId AND c.product.id = :productId";

		Query deleteCartQuery = entityManager.createQuery(deleteQuery).setParameter("customerId", customerId)
				.setParameter("productId", productId);

		int deletedCount = deleteCartQuery.executeUpdate();

		if (deletedCount > 0) {
			System.out.println("deleted from cart");
			return productId;
		} else {
			return -1;
		}
	}

	/*
	 * "Select new eStoreProduct.utility.ProductStockPrice(p.id,p.title,p.brand,p.imageUrl,p.description,ps.price," +
	 * "c.quantity)" +
	 * "from eStoreProduct.model.admin.entities.productsModel p,eStoreProduct.model.customer.entities.cartModel c ," +
	 * "eStoreProduct.model.admin.entities.productStockModel ps," +
	 * "eStoreProduct.model.customer.entities.custCredModel cust where cust.custId=cust.custId = :custId and ps.product=p.id"
	 * ;
	 */

	public List<ProductStockPrice> getCartProds(int cust_id) {
		String selectQuery = "SELECT new eStoreProduct.utility.ProductStockPrice(p.id, p.title, p.brand, p.imageUrl, p.description, ps.price, c.quantity) "
				+ "FROM productsModel p " + "JOIN p.cartItems c "
				+ "JOIN eStoreProduct.model.admin.entities.productStockModel ps "
				+ " WHERE c.customer.custId = :custId";

		Query cartProductsQuery = entityManager.createQuery(selectQuery, ProductStockPrice.class).setParameter("custId",
				cust_id);

		try {
			List<ProductStockPrice> cproducts = cartProductsQuery.getResultList();
			System.out.println(cproducts.toString());
			return cproducts;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Transactional
	public int updateQty(cartModel cm) {
		String updateQuery = "UPDATE eStoreProduct.model.customer.entities.cartModel c "
				+ "SET c.quantity = :quantity WHERE c.customer.custId = :customerId AND c.product.id = :productId";

		Query updateQtyQuery = entityManager.createQuery(updateQuery).setParameter("quantity", cm.getQuantity())
				.setParameter("customerId", cm.getCustomer().getCustId())
				.setParameter("productId", cm.getProduct().getId());

		int updatedCount = updateQtyQuery.executeUpdate();

		if (updatedCount > 0) {
			System.out.println("updated in cart");
			return updatedCount;
		} else {
			return -1;
		}
	}

	@Transactional
	public int updateinsert(List<ProductStockPrice> products, int cust_id) {
		int r = -1;
		for (ProductStockPrice ps : products) {
			cartModel cartItem = new cartModel();
			// int cartId=getId()
			System.out.println("updateinsert 1:" + cust_id);
			cartItem.setCustomer(entityManager.find(custCredModel.class, cust_id));
			System.out.println("updateinsert 2");

			cartItem.setProduct(entityManager.find(productsModel.class, ps.getProd_id()));
			System.out.println("updateinsert 3");

			cartItem.setQuantity(ps.getQuantity());
			entityManager.persist(cartItem);
			r++;
		}
		return r;
	}

	public ServiceableRegion getRegionByPincode(int pincode) {
		String selectQuery = "SELECT r FROM ServiceableRegion r WHERE :pincode BETWEEN r.regionPinFrom AND r.regionPinTo";

		Query regionQuery = entityManager.createQuery(selectQuery, ServiceableRegion.class).setParameter("pincode",
				pincode);

		List<ServiceableRegion> regions = regionQuery.getResultList();

		if (!regions.isEmpty()) {
			return regions.get(0);
		} else {
			return null;
		}
	}

	@Override
	public int getId(int productId, int customerId) {
		// TODO Auto-generated method stub
		String selectQuery = "SELECT new Integer(cm.cartId) FROM  eStoreProduct.model.customer.entities.cartModel cm where"
				+ " cm.customer.custId = :customerId  AND cm.product.id = :productId";
		/*
		 * String updateQuery = "UPDATE eStoreProduct.model.customer.entities.cartModel c " +
		 * "SET c.quantity = :quantity WHERE c.customer.custId = :customerId AND c.product.id = :productId";
		 */
		Query updateQtyQuery = entityManager.createQuery(selectQuery).setParameter("customerId", customerId)
				.setParameter("productId", productId);
		List<Integer> id = updateQtyQuery.getResultList();
		return id.get(0);
	}

}

/*
 * public hsnModel getHSNCodeByProductId(int prodId) { String sql = "SELECT hsn_code, sgst, igst, cgst, gst " +
 * "FROM slam_HSN_Code " + "WHERE hsn_code = ?";
 * 
 * return jdbcTemplate.queryForObject(sql, new Object[] { prodId }, (resultSet, rowNum) -> { hsnModel hsnCodeModel = new
 * hsnModel(); hsnCodeModel.setHsn_code(resultSet.getInt("hsn_code"));
 * hsnCodeModel.setSgst(resultSet.getDouble("sgst")); hsnCodeModel.setIgst(resultSet.getDouble("igst"));
 * hsnCodeModel.setCgst(resultSet.getDouble("cgst")); hsnCodeModel.setGst(resultSet.getDouble("gst")); return
 * hsnCodeModel; }); }
 */

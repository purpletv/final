package eStoreProduct.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import eStoreProduct.DAO.admin.CategoryReportDAO;
import eStoreProduct.DAO.admin.OrderRepDAO;
import eStoreProduct.model.admin.entities.SlamOrderModel;
import eStoreProduct.model.admin.output.CategoryReportViewModel;

@Controller

public class adminReportController {

	private OrderRepDAO odao;
	private CategoryReportDAO crd;

	@Autowired
	public adminReportController(OrderRepDAO odao, CategoryReportDAO crd) {
		this.odao = odao;
		this.crd = crd;
	}

	@GetMapping("/generateGSTReport")
	public String generateGSTReport(Model model) {
		List<SlamOrderModel> om = odao.getAllOrders();
		model.addAttribute("orderReport", om);
		return "Reports";
	}

	@GetMapping("/categoryReport")
	public String categoryReport(Model model) {
		// List<orderModel> om=odao.getAllOrders();
		List<CategoryReportViewModel> cl = crd.getCatRep();
		model.addAttribute("categoryReport", cl);
		return "catReports";
	}
	/*
	 * @GetMapping("/categoryReportSort") public String categoryReportSort(Model model) { //List<orderModel>
	 * om=odao.getAllOrders(); List<CategoryReportViewModel> cl= model.addAttribute("categoryReport", cl); return
	 * "catReports"; }
	 */
}

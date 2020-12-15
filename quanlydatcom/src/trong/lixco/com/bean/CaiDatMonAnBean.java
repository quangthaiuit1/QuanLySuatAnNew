package trong.lixco.com.bean;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.jboss.logging.Logger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.bean.staticentity.DateUtil;
import trong.lixco.com.bean.staticentity.MessageView;
import trong.lixco.com.bean.staticentity.Notification;
import trong.lixco.com.bean.staticentity.ShiftsUtil;
import trong.lixco.com.ejb.service.CategoryFoodService;
import trong.lixco.com.ejb.service.FoodDayByDayService;
import trong.lixco.com.ejb.service.OrderAndFoodByDateService;
import trong.lixco.com.ejb.service.ShiftsService;
import trong.lixco.com.jpa.entity.CategoryFood;
import trong.lixco.com.jpa.entity.FoodByDay;
import trong.lixco.com.jpa.entity.OrderFood;
import trong.lixco.com.jpa.entity.Shifts;

@javax.faces.bean.ManagedBean
@ViewScoped
public class CaiDatMonAnBean extends AbstractBean<OrderFood> {
	private static final long serialVersionUID = 1L;

	private Member member;
	private int shifts = 1;
	private Date dateSearch;
	private List<FoodByDay> foodsByDate;
	private List<CategoryFood> categoryFoods;
	private List<CategoryFood> categoryFoodsFilter;
	private CategoryFood categoryFoodSelected;
	private List<CategoryFood> categoryFoodsSelected;
	private CategoryFood categoryFoodUpdate;
	private FoodByDay foodByDayUpdate;
	private java.sql.Date dateSearchSQL;
	private int shifts1;
	private int shifts2;
	private int shifts3;

	private Date dateDetailFromDate;
	private Date dateDetailToDate;

	@EJB
	private FoodDayByDayService FOOD_DAYBYDAY_SERVICE;
	@EJB
	private CategoryFoodService CATEGORY_FOOD_SERVICE;
	@EJB
	private ShiftsService SHIFTS_SERVICE;
	@EJB
	private OrderAndFoodByDateService ORDER_FOOD_BY_DAY_SERVICE;

	@Override
	protected void initItem() {
		Date currentDate00 = DateUtil.DATE_WITHOUT_TIME(new Date());
		// init variable
		dateSearch = currentDate00;
		dateDetailFromDate = currentDate00;
		dateDetailToDate = currentDate00;
		try {
			foodsByDate = FOOD_DAYBYDAY_SERVICE.findByDate(dateSearch, shifts);
		} catch (Exception e) {
		}
		member = getAccount().getMember();
		categoryFoods = CATEGORY_FOOD_SERVICE.findAllNew(); 
		shifts1 = ShiftsUtil.SHIFTS1_ID;
		shifts2 = ShiftsUtil.SHIFTS2_ID;
		shifts3 = ShiftsUtil.SHIFTS3_ID;

	}

	public void convertCategoryFoodToSelected() {
		try {
			List<CategoryFood> cfTemp = new ArrayList<>();
			for (FoodByDay f : foodsByDate) {
				cfTemp.add(f.getCategory_food());
			}
			categoryFoodsSelected = new ArrayList<>();
			if (!cfTemp.isEmpty()) {
				this.categoryFoodsSelected.addAll(cfTemp);
			}
		} catch (Exception e) {
		}
	}

	public void dateSearchChange() {
		try {
			foodsByDate = FOOD_DAYBYDAY_SERVICE.findByDate(dateSearch, shifts);
		} catch (Exception e) {
		}

	}

	public void shiftChange() {
		try {
			foodsByDate = FOOD_DAYBYDAY_SERVICE.findByDate(dateSearch, shifts);
		} catch (Exception e) {
		}
	}

	public void changeFoodAjax(FoodByDay item) {
		foodByDayUpdate = item;
	}

	public void handleChangeFood() {
		foodByDayUpdate.setCategory_food(categoryFoodUpdate);
		FoodByDay update = FOOD_DAYBYDAY_SERVICE.update(foodByDayUpdate);
		if (update != null) {
			foodByDayUpdate = new FoodByDay();
			categoryFoodUpdate = new CategoryFood();
			MessageView.INFO("Thành công");
			return;
		} else {
			MessageView.ERROR("Lỗi");
		}
	}

	public void deleteRow(FoodByDay item) {
		try {
			// // tim xem co ai dang ky mon an hay chua -> neu chua thi cho xoa
			// List<OrderAndFoodByDate> ofs =
			// ORDER_FOOD_BY_DAY_SERVICE.find(item.getId());
			// chua ai dang ky
			boolean check = FOOD_DAYBYDAY_SERVICE.delete(item);
			if (check) {
				Notification.NOTI_SUCCESS("Xóa thành công");
				foodsByDate = FOOD_DAYBYDAY_SERVICE.findByDate(dateSearch, shifts);
			} else {
				Notification.NOTI_ERROR("Xóa không thành công");
			}
			// } else {
			// Notification.NOTI_ERROR("Lỗi! Đã có người đăng ký món ăn");
			// }
		} catch (Exception e) {
		}
	}

	public void showPDFDetailFromDateToDate() {
		try {
			List<FoodByDay> foods = FOOD_DAYBYDAY_SERVICE.findByDayToDaySortByDate(dateDetailFromDate,
					dateDetailToDate);
			if (!foods.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/chitietmonanhangngay.jasper");
				// check neu list rong~
				if (!foods.isEmpty()) {
					JRDataSource beanDataSource = new JRBeanCollectionDataSource(foods);
					Map<String, Object> importParam = new HashMap<String, Object>();
					String image = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/gfx/lixco_logo.png");
					importParam.put("logo", image);
					JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
					FacesContext facesContext = FacesContext.getCurrentInstance();
					OutputStream outputStream;
					outputStream = facesContext.getExternalContext().getResponseOutputStream();
					JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
					facesContext.responseComplete();
				} else {
					Notification.NOTI_ERROR("Không có dữ liệu");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleChooseFood() {
		if (foodsByDate != null) {
			for (int i = 0; i < categoryFoodsSelected.size(); i++) {
				List<FoodByDay> queryChecked = FOOD_DAYBYDAY_SERVICE.findByDate(dateSearch, shifts,
						categoryFoodsSelected.get(i).getId());
				if (queryChecked.isEmpty()) {
					FoodByDay foodNew = new FoodByDay();
					foodNew.setCategory_food(categoryFoodsSelected.get(i));
					foodNew.setFood_date(dateSearch);
					Shifts sTemp = SHIFTS_SERVICE.findById(shifts);
					foodNew.setShifts(sTemp);
					foodNew.setCreatedUser(member.getCode());
					foodNew.setCreatedDate(new java.util.Date());
					try {
						FOOD_DAYBYDAY_SERVICE.create(foodNew);
					} catch (Exception e) {
					}
				}
			}
			foodsByDate = FOOD_DAYBYDAY_SERVICE.findByDate(dateSearch, shifts);
			// Notification.NOTI_SUCCESS("Thành công");
			MessageView.INFO("Thành công");
		}
	}

	@Override
	protected Logger getLogger() {
		return null;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Date getDateSearch() {
		return dateSearch;
	}

	public void setDateSearch(Date dateSearch) {
		this.dateSearch = dateSearch;
	}

	public int getShifts() {
		return shifts;
	}

	public void setShifts(int shifts) {
		this.shifts = shifts;
	}

	public void setFoodsByDate(List<FoodByDay> foodsByDate) {
		this.foodsByDate = foodsByDate;
	}

	public List<FoodByDay> getFoodsByDate() {
		return foodsByDate;
	}

	public List<CategoryFood> getCategoryFoods() {
		return categoryFoods;
	}

	public void setCategoryFoods(List<CategoryFood> categoryFoods) {
		this.categoryFoods = categoryFoods;
	}

	public CategoryFood getCategoryFoodSelected() {
		return categoryFoodSelected;
	}

	public void setCategoryFoodSelected(CategoryFood categoryFoodSelected) {
		this.categoryFoodSelected = categoryFoodSelected;
	}

	public List<CategoryFood> getCategoryFoodsFilter() {
		return categoryFoodsFilter;
	}

	public void setCategoryFoodsFilter(List<CategoryFood> categoryFoodsFilter) {
		this.categoryFoodsFilter = categoryFoodsFilter;
	}

	public List<CategoryFood> getCategoryFoodsSelected() {
		return categoryFoodsSelected;
	}

	public void setCategoryFoodsSelected(List<CategoryFood> categoryFoodsSelected) {
		this.categoryFoodsSelected = categoryFoodsSelected;
	}

	public int getShifts1() {
		return shifts1;
	}

	public void setShifts1(int shifts1) {
		this.shifts1 = shifts1;
	}

	public int getShifts2() {
		return shifts2;
	}

	public void setShifts2(int shifts2) {
		this.shifts2 = shifts2;
	}

	public int getShifts3() {
		return shifts3;
	}

	public void setShifts3(int shifts3) {
		this.shifts3 = shifts3;
	}

	public Date getDateDetailFromDate() {
		return dateDetailFromDate;
	}

	public void setDateDetailFromDate(Date dateDetailFromDate) {
		this.dateDetailFromDate = dateDetailFromDate;
	}

	public Date getDateDetailToDate() {
		return dateDetailToDate;
	}

	public void setDateDetailToDate(Date dateDetailToDate) {
		this.dateDetailToDate = dateDetailToDate;
	}

	public CategoryFood getCategoryFoodUpdate() {
		return categoryFoodUpdate;
	}

	public void setCategoryFoodUpdate(CategoryFood categoryFoodUpdate) {
		this.categoryFoodUpdate = categoryFoodUpdate;
	}
}

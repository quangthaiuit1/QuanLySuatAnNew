package trong.lixco.com.bean;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.bean.entities.BaoChenhLech;
import trong.lixco.com.bean.entities.ChiTietDuBaoSuatAn;
import trong.lixco.com.bean.entities.OrderFoodReport;
import trong.lixco.com.bean.entities.TimekeepingData;
import trong.lixco.com.bean.entities.TimekeepingDataService;
import trong.lixco.com.bean.staticentity.DateUtil;
import trong.lixco.com.bean.staticentity.MessageView;
import trong.lixco.com.bean.staticentity.Notification;
import trong.lixco.com.bean.staticentity.ShiftsUtil;
import trong.lixco.com.ejb.service.FoodCustomerService;
import trong.lixco.com.ejb.service.FoodNhaAnService;
import trong.lixco.com.ejb.service.FoodOverTimeService;
import trong.lixco.com.ejb.service.OrderAndFoodByDateService;
import trong.lixco.com.ejb.service.OrderFoodService;
import trong.lixco.com.ejb.service.QuantityFoodService;
import trong.lixco.com.ejb.service.ReportFoodByDayService;
import trong.lixco.com.ejb.service.ShiftsService;
import trong.lixco.com.jpa.entity.FoodCustomer;
import trong.lixco.com.jpa.entity.FoodNhaAn;
import trong.lixco.com.jpa.entity.FoodOverTime;
import trong.lixco.com.jpa.entity.OrderAndFoodByDate;
import trong.lixco.com.jpa.entity.OrderFood;
import trong.lixco.com.jpa.entity.QuantityFood;
import trong.lixco.com.jpa.entity.ReportFoodByDay;
import trong.lixco.com.jpa.entity.Shifts;
import trong.lixco.com.servicepublic.EmployeeDTO;
import trong.lixco.com.servicepublic.EmployeeServicePublic;
import trong.lixco.com.servicepublic.EmployeeServicePublicProxy;

@Named
@ViewScoped
public class BaoCaoBean extends AbstractBean<OrderFood> {
	private static final long serialVersionUID = 1L;
	SimpleDateFormat formatter;
	private String loaiBaoCao;
	private List<FoodNhaAn> foodNhaAns;
	private int shifts;
	private Date dateSearch;
	private int shifts1;
	private int shifts2;
	private int shifts3;

	// bao cao chinh xac
	private int shiftsExactly;
	private Date dateSearchExactly;

	// du bao suat an
	private Date fromDate;
	private Date toDate;
	// chi tiet
	private Date fromDateDetail;
	private Date toDateDetail;

	// ket qua da an
	private Date fromDateAte;
	private Date toDateAte;

	private boolean[] checkedRenderView;
	private int valueChecked;

	// bao cao chinh xacs
	private int tongSoLuong = 0;
	private int soLuongDangKy = 0;
	private int soLuongDangKyThem = 0;
	private int soLuongMonChay = 0;
	private int soLuongMonMan = 0;

	private QuantityFood quantitySelected;
	private Member member;
	private List<FoodCustomer> foodCustomersByQuantityFood;
	private int totalTangCa = 0;
	private List<Shifts> allShifts;
	private Shifts shiftsSelected;

	@Inject
	private FoodNhaAnService FOOD_NHA_AN_SERVICE;
	@Inject
	private OrderFoodService ORDER_FOOD_SERVICE;
	@Inject
	private OrderAndFoodByDateService ORDER_AND_FOOD_BY_DATE_SERVICE;
	@Inject
	private ReportFoodByDayService REPORT_FOOD_BY_DAY_SERVICE;
	@Inject
	private QuantityFoodService QUANTITY_FOOD_SERVICE;
	@Inject
	private ShiftsService SHIFTS_SERVICE;
	@Inject
	private FoodCustomerService FOOD_CUSTOMER_SERVICE;
	@Inject
	private FoodOverTimeService FOOD_OVER_TIME_SERVICE;

	EmployeeServicePublic EMPLOYEE_SERVICE_PUBLIC;
	DepartmentServicePublic DEPARTMENT_SERVICE_PUBLIC;

	@Override
	protected void initItem() {
		DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
		EMPLOYEE_SERVICE_PUBLIC = new EmployeeServicePublicProxy();
		formatter = new SimpleDateFormat("dd-MM-yyyy");
		dateSearch = new Date();
		shifts1 = ShiftsUtil.SHIFTS1_ID;
		shifts2 = ShiftsUtil.SHIFTS2_ID;
		shifts3 = ShiftsUtil.SHIFTS3_ID;
		// handle shifts
		allShifts = SHIFTS_SERVICE.findAll();
		if (allShifts.size() != 0) {
			shiftsSelected = allShifts.get(0);
		}
		// du bao suat an
		Date currentDate00 = new Date();
		// currentDate00 = DateUtil.SET_HHMMSS_00(currentDate00);
		currentDate00 = DateUtil.DATE_WITHOUT_TIME(currentDate00);
		fromDate = currentDate00;
		toDate = currentDate00;

		fromDateDetail = currentDate00;
		toDateDetail = currentDate00;

		fromDateAte = currentDate00;
		toDateAte = currentDate00;

		dateSearchExactly = currentDate00;
		checkedRenderView = new boolean[9];
		for (int i = 0; i < checkedRenderView.length; i++) {
			checkedRenderView[i] = false;
		}
		quantitySelected = new QuantityFood();
		member = getAccount().getMember();
	}

	public void handleRenderView() {
		// set false toan bo view
		for (int i = 0; i < checkedRenderView.length; i++) {
			checkedRenderView[i] = false;
		}
		if (valueChecked != 0) {
			for (int i = 1; i < 8; i++) {
				if (valueChecked == i) {
					checkedRenderView[i - 1] = true;
				} else {
					checkedRenderView[i - 1] = false;
				}
			}
			PrimeFaces.current().ajax().update("formBaoCao");
		} else {
			// set false toan bo view
			for (int i = 0; i < checkedRenderView.length; i++) {
				checkedRenderView[i] = false;
			}
			PrimeFaces.current().ajax().update("formBaoCao");
			return;
		}
	}

	public void handleAjaxChangeTongSoLuongSuatAn() {
		int quantityPlus = quantitySelected.getTongsuatan() - quantitySelected.getSuatandadangky();
		quantitySelected.setTongsuatandkthem(quantityPlus);
		int quantitySuatAnMan = quantitySelected.getTongsuatandkthem() - quantitySelected.getSuatanchay();
		quantitySelected.setSuatanman(quantitySuatAnMan);
	}

	public void handleAjaxChooseDateExactly() {
		totalTangCa = 0;
		quantitySelected = new QuantityFood();
		if (shiftsExactly == 0) {
			Notification.NOTI_WARN("Vui lòng chọn ca làm việc");
			return;
		}
		List<QuantityFood> quantityFoods = QUANTITY_FOOD_SERVICE.find(dateSearchExactly, shiftsExactly);
		// tim so luong food tang ca
		List<FoodOverTime> foodOT = FOOD_OVER_TIME_SERVICE.find(dateSearchExactly, shiftsExactly, null);
		if (foodOT.size() != 0) {
			if (foodOT.get(0).getOver_time().isIs_duyet()) {
				totalTangCa = foodOT.size();
			}
		}
		// da luu thong tin ca xuong duoi
		if (quantityFoods.size() != 0) {
			quantitySelected = quantityFoods.get(0);
		}
		// convert array to list
		List<TimekeepingData> listDataVerify = new ArrayList<>();
		if (quantityFoods.size() == 0) {
			try {
				DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
				String dateSearchString = formatter1.format(dateSearchExactly);
				TimekeepingData[] arr = null;
				boolean checkShifts0 = false;
				if (shiftsExactly == 0) {
					// 8C la ca sang 6h->6h toi -> bao gom nhan vien van phong
					// va cong nhan
					arr = TimekeepingDataService.timtheongay(dateSearchString);
					this.shiftsExactly = 1;
					checkShifts0 = true;
				}
				if (shiftsExactly == ShiftsUtil.SHIFTS1_ID && !checkShifts0) {
					// 8C la ca sang 6h->6h toi // gom nv van phong va cong nhan
					arr = TimekeepingDataService.timtheongay(dateSearchString);
					// System.out.println("arr: " + arr.length);
					//
					// TimekeepingData[] arrCD =
					// TimekeepingDataService.searchByDateAndWorkTemp(dateSearchString,
					// "CD");
					// if (arrCD != null) {
					// System.out.println("arrCD: " + arrCD.length);
					// }
					//
					// TimekeepingData[] arr8C =
					// TimekeepingDataService.searchByDateAndWorkTemp(dateSearchString,
					// "8C");
					// if (arr8C != null) {
					// System.out.println("arr8C: " + arr8C.length);
					// }
					//
					// // loc ra nhan vien di ca CD
					// List<TimekeepingData> arrAfterFilter = new ArrayList<>();
					// for (int i = 0; i < arr.length; i++) {
					// if (arr[i].getWorkTemp() == null) {
					// System.out.println("index: " + i);
					// System.out.println(arr[i].getCode());
					// }
					// if ((arr[i].getWorkTemp() != null &&
					// arr[i].getWorkTemp().equals("8"))
					// || (arr[i].getWorkTemp() != null &&
					// arr[i].getWorkTemp().equals("8C"))) {
					// arrAfterFilter.add(arr[i]);
					// }
					// }
					// arr = arrAfterFilter.toArray(new
					// TimekeepingData[arrAfterFilter.size()]);
				}
				if (shiftsExactly == ShiftsUtil.SHIFTS2_ID) {
					// nhan vien di ca
					arr = TimekeepingDataService.searchByDateAndWorkTemp(dateSearchString, "8C");
					List<TimekeepingData> arrListTemp = new ArrayList<>();
					if (arr != null) {
						for (int i = 0; i < arr.length; i++) {
							// check dieu kien co di ca hay khong
							if (arr[i].isWorkShift()) {
								arrListTemp.add(arr[i]);
							}
						}
						arr = arrListTemp.toArray(new TimekeepingData[arrListTemp.size()]);
					}
				}
				if (shiftsExactly == ShiftsUtil.SHIFTS3_ID) {
					// cd la ca chieu 6h toi-> 6h sang
					arr = TimekeepingDataService.searchByDateAndWorkTemp(dateSearchString, "CD");
				}
				if (arr != null) {
					listDataVerify = Arrays.asList(arr);
				}
				if (!listDataVerify.isEmpty()) {
					// set ngay va ca
					quantitySelected.setFood_date(dateSearchExactly);
					Shifts sTemp = SHIFTS_SERVICE.findById(shiftsExactly);
					quantitySelected.setShifts(sTemp);
					quantitySelected.setTongsuatan(listDataVerify.size());
					// list de gan qua report
					List<OrderAndFoodByDate> ofs = new ArrayList<>();
					List<OrderAndFoodByDate> ofsTemp = ORDER_AND_FOOD_BY_DATE_SERVICE
							.findByDateSortByEmplName(dateSearchExactly, shiftsExactly);
					// kiem tra nv hom do co di lam khong
					for (int i = 0; i < ofsTemp.size(); i++) {
						boolean isWork = false;
						for (int j = 0; j < listDataVerify.size(); j++) {
							if (ofsTemp.get(i).getOrder_food().getEmployee_id()
									.equals(listDataVerify.get(j).getCodeOld())) {
								isWork = true;
								break;
							}
						}
						if (isWork) {
							ofs.add(ofsTemp.get(i));
						}
					}
					// tim so luong food them
					int foodPlus = 0;
					if (listDataVerify.size() > ofs.size() || listDataVerify.size() == ofs.size()) {
						// so luong mon an duoc dang ky
						quantitySelected.setSuatandadangky(ofs.size());
						foodPlus = listDataVerify.size() - ofs.size();
						// so luong mon an cong nhan hoac nhung nv khong dang ky
						quantitySelected.setTongsuatandkthem(foodPlus);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// handle suat an khach
	public void handleShowFoodCustomer() {
		// check quantity food da co hay chua. Neu chua thi se tao list moi
		if (quantitySelected.getId() == null) {
			foodCustomersByQuantityFood = new ArrayList<>();
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('wdvDialogFoodCustomer').show();");
			return;
		}
		if (quantitySelected.getId() != null) {
			foodCustomersByQuantityFood = FOOD_CUSTOMER_SERVICE.find(quantitySelected.getId());
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('wdvDialogFoodCustomer').show();");
		}
	}

	// handle update table food customer
	public void updateFoodCustomer() {
		if (quantitySelected.getId() == null) {
			Notification.NOTI_ERROR(
					"Vui lòng chọn ngày và chọn ca hoặc lưu thông tin chi tiết* trước khi cập nhật suất ăn khách");
			return;
		}
		if (quantitySelected.getId() != null) {
			boolean error = false;
			for (FoodCustomer f : foodCustomersByQuantityFood) {
				if (f.getId() == null) {
					f.setCreatedDate(new Date());
					f.setCreatedUser(member.getCode());
					f.setQuantity_food(quantitySelected);
					FoodCustomer fCheck = FOOD_CUSTOMER_SERVICE.create(f);
					if (fCheck == null) {
						Notification.NOTI_ERROR("Lỗi");
						error = true;
						return;
					}
				}
				// da co roi
				if (f.getId() != null) {
					f.setModifiedDate(new Date());
					f.setModifiedUser(member.getCode());
					FoodCustomer fCheck = FOOD_CUSTOMER_SERVICE.update(f);
					if (fCheck == null) {
						Notification.NOTI_ERROR("Lỗi");
						error = true;
					}
				}
			}
			if (error) {
				Notification.NOTI_ERROR("Lỗi");
				return;
			}
			// query toan bo list food customer sau do cong toan bo so luong ->
			// so luong
			// tong ben bang quantity food
			List<FoodCustomer> foodCustomers = FOOD_CUSTOMER_SERVICE.find(quantitySelected.getId());
			int total = 0;
			for (FoodCustomer fc : foodCustomers) {
				total = total + fc.getQuantity();
			}
			quantitySelected.setTongsuatankhach(total);
			QUANTITY_FOOD_SERVICE.update(quantitySelected);
			// Notification.NOTI_SUCCESS("Thành công");
			MessageView.INFO("Thành công");
		}
	}

	// them row moi food customer
	public void addNewRowFoodCustomer() {
		FoodCustomer fc = new FoodCustomer();
		fc.setQuantity(1);
		foodCustomersByQuantityFood.add(fc);
	}

	// handle hien chinh xac so lieu
	public void handleAjaxChooseShifts() {
		totalTangCa = 0;
		quantitySelected = new QuantityFood();
		List<QuantityFood> quantityFoods = QUANTITY_FOOD_SERVICE.find(dateSearchExactly, shiftsExactly);
		// tim so luong food tang ca
		List<FoodOverTime> foodOT = FOOD_OVER_TIME_SERVICE.find(dateSearchExactly, shiftsExactly, null);
		if (foodOT.size() != 0) {
			if (foodOT.get(0).getOver_time().isIs_duyet()) {
				totalTangCa = foodOT.size();
			}
		}
		// da luu thong tin ca xuong duoi
		if (quantityFoods.size() != 0) {
			quantitySelected = quantityFoods.get(0);
		}
		// convert array to list
		List<TimekeepingData> listDataVerify = new ArrayList<>();
		if (quantityFoods.size() == 0) {
			try {
				DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
				String dateSearchString = formatter1.format(dateSearchExactly);
				TimekeepingData[] arr = null;
				boolean checkShifts0 = false;
				if (shiftsExactly == 0) {
					// 8C la ca sang 6h->6h toi -> bao gom nhan vien van phong
					// va cong nhan
					arr = TimekeepingDataService.timtheongay(dateSearchString);
					this.shiftsExactly = 1;
					checkShifts0 = true;
				}
				if (shiftsExactly == ShiftsUtil.SHIFTS1_ID && !checkShifts0) {
					// 8C la ca sang 6h->6h toi // gom nv van phong va cong nhan
					arr = TimekeepingDataService.timtheongay(dateSearchString);
					// System.out.println("arr: " + arr.length);
					//
					// TimekeepingData[] arrCD =
					// TimekeepingDataService.searchByDateAndWorkTemp(dateSearchString,
					// "CD");
					// if (arrCD != null) {
					// System.out.println("arrCD: " + arrCD.length);
					// }
					//
					// TimekeepingData[] arr8C =
					// TimekeepingDataService.searchByDateAndWorkTemp(dateSearchString,
					// "8C");
					// if (arr8C != null) {
					// System.out.println("arr8C: " + arr8C.length);
					// }
					//
					// // loc ra nhan vien di ca CD
					// List<TimekeepingData> arrAfterFilter = new ArrayList<>();
					// for (int i = 0; i < arr.length; i++) {
					// if (arr[i].getWorkTemp() == null) {
					// System.out.println("index: " + i);
					// System.out.println(arr[i].getCode());
					// }
					// if ((arr[i].getWorkTemp() != null &&
					// arr[i].getWorkTemp().equals("8"))
					// || (arr[i].getWorkTemp() != null &&
					// arr[i].getWorkTemp().equals("8C"))) {
					// arrAfterFilter.add(arr[i]);
					// }
					// }
					// arr = arrAfterFilter.toArray(new
					// TimekeepingData[arrAfterFilter.size()]);
				}
				if (shiftsExactly == ShiftsUtil.SHIFTS2_ID) {
					// nhan vien di ca
					arr = TimekeepingDataService.searchByDateAndWorkTemp(dateSearchString, "8C");
					List<TimekeepingData> arrListTemp = new ArrayList<>();
					if (arr != null) {
						for (int i = 0; i < arr.length; i++) {
							// check dieu kien co di ca hay khong
							if (arr[i].isWorkShift()) {
								arrListTemp.add(arr[i]);
							}
						}
						arr = arrListTemp.toArray(new TimekeepingData[arrListTemp.size()]);
					}
				}
				if (shiftsExactly == ShiftsUtil.SHIFTS3_ID) {
					// cd la ca chieu 6h toi-> 6h sang
					arr = TimekeepingDataService.searchByDateAndWorkTemp(dateSearchString, "CD");
				}
				if (arr != null) {
					listDataVerify = Arrays.asList(arr);
				}
				if (!listDataVerify.isEmpty()) {
					// set ngay va ca
					quantitySelected.setFood_date(dateSearchExactly);
					Shifts sTemp = SHIFTS_SERVICE.findById(shiftsExactly);
					quantitySelected.setShifts(sTemp);
					quantitySelected.setTongsuatan(listDataVerify.size());
					// list de gan qua report
					List<OrderAndFoodByDate> ofs = new ArrayList<>();
					List<OrderAndFoodByDate> ofsTemp = ORDER_AND_FOOD_BY_DATE_SERVICE
							.findByDateSortByEmplName(dateSearchExactly, shiftsExactly);
					// kiem tra nv hom do co di lam khong
					for (int i = 0; i < ofsTemp.size(); i++) {
						boolean isWork = false;
						for (int j = 0; j < listDataVerify.size(); j++) {
							if (ofsTemp.get(i).getOrder_food().getEmployee_id()
									.equals(listDataVerify.get(j).getCodeOld())) {
								isWork = true;
								break;
							}
						}
						if (isWork) {
							ofs.add(ofsTemp.get(i));
						}
					}
					// tim so luong food them
					int foodPlus = 0;
					if (listDataVerify.size() > ofs.size() || listDataVerify.size() == ofs.size()) {
						// so luong mon an duoc dang ky
						quantitySelected.setSuatandadangky(ofs.size());
						foodPlus = listDataVerify.size() - ofs.size();
						// so luong mon an cong nhan hoac nhung nv khong dang ky
						quantitySelected.setTongsuatandkthem(foodPlus);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updateSoLuongMonMan(ValueChangeEvent event) {
		try {
			quantitySelected.setSuatanman((Integer) event.getNewValue());
			handleAjaxChangeInputMonMan();
			PrimeFaces.current().ajax().update(":formBaoCao:panelGridMonChay");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleAjaxChangeInputMonMan() {
		int soluongmonchay = quantitySelected.getTongsuatandkthem() - quantitySelected.getSuatanchay();
		this.quantitySelected.setSuatanman(soluongmonchay);
	}

	private static XSSFCellStyle createStyleForTitle(XSSFWorkbook workbook) {
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);
		return style;
	}

	// tong hop toan bo can bo CNV da an
	public void baoCaoChiTietKetQuaPDF() {
		try {
			// list de gan qua report
			List<FoodNhaAn> foods = FOOD_NHA_AN_SERVICE.findByDayToDaySortByDateAndShiftsAndFoodName(fromDateAte,
					toDateAte, null);
			if (!foods.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/bcchitietsuatan.jasper");
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

	public void deleteRowFoodCustomer(FoodCustomer item) {
		boolean checkDelete = FOOD_CUSTOMER_SERVICE.delete(item);
		if (checkDelete) {
			foodCustomersByQuantityFood = FOOD_CUSTOMER_SERVICE.find(quantitySelected.getId());
			int total = 0;
			for (FoodCustomer f : foodCustomersByQuantityFood) {
				total = total + f.getQuantity();
			}
			quantitySelected.setTongsuatankhach(total);
			QuantityFood q = QUANTITY_FOOD_SERVICE.update(quantitySelected);
			if (q != null) {
				Notification.NOTI_SUCCESS("Thành công");
			} else {
				Notification.NOTI_ERROR("Lỗi");
			}

		} else {
			Notification.NOTI_ERROR("Lỗi");
		}
	}

	public void baoCaoChinhXacSuatAnPDF() {
		try {
			// long startTimeAll = System.currentTimeMillis();
			DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
			String dateSearchString = formatter1.format(dateSearchExactly);
			TimekeepingData[] arr = null;
			// tim ca lam viec
			String shiftsName = "";
			if (shiftsExactly == 0) {
				Notification.NOTI_ERROR("Vui lòng chọn ca");
				return;
			}
			// set truong hop neu khong chon ca nao
			if (shiftsExactly == ShiftsUtil.SHIFTS1_ID) {
				shiftsName = ShiftsUtil.SHIFTS1_NAME;
				// 8C la ca sang 6h->6h toi // gom nv van phong va cong nhan
				// long startTime = System.currentTimeMillis();
				arr = TimekeepingDataService.timtheongay(dateSearchString);
				// long end = System.currentTimeMillis() - startTime;
				// System.out.println("Thoi gian query: " + end);
			}
			if (shiftsExactly == ShiftsUtil.SHIFTS2_ID) {
				shiftsName = ShiftsUtil.SHIFTS2_NAME;
				// nhan vien di ca
				arr = TimekeepingDataService.searchByDateAndWorkTemp(dateSearchString, "8C");
				List<TimekeepingData> arrListTemp = new ArrayList<>();
				if (arr != null) {
					for (int i = 0; i < arr.length; i++) {
						// check dieu kien co di ca hay khong
						if (arr[i].isWorkShift()) {
							arrListTemp.add(arr[i]);
						}
					}
					arr = arrListTemp.toArray(new TimekeepingData[arrListTemp.size()]);
				}
			}
			if (shiftsExactly == ShiftsUtil.SHIFTS3_ID) {
				shiftsName = ShiftsUtil.SHIFTS3_NAME;
				// cd la ca chieu 6h toi-> 6h sang
				arr = TimekeepingDataService.searchByDateAndWorkTemp(dateSearchString, "CD");
			}
			// convert array to list
			List<TimekeepingData> listDataVerify = new ArrayList<>();
			if (arr != null) {
				listDataVerify = Arrays.asList(arr);
			}
			if (!listDataVerify.isEmpty()) {
				// list de gan qua report
				List<OrderAndFoodByDate> ofs = new ArrayList<>();
				List<OrderAndFoodByDate> ofsTemp = ORDER_AND_FOOD_BY_DATE_SERVICE
						.findByDateSortByEmplName(dateSearchExactly, shiftsExactly);
				// kiem tra nv hom do co di lam khong
				for (int i = 0; i < ofsTemp.size(); i++) {
					boolean isWork = false;
					for (int j = 0; j < listDataVerify.size(); j++) {
						if (ofsTemp.get(i).getOrder_food().getEmployee_id()
								.equals(listDataVerify.get(j).getCodeOld())) {
							isWork = true;
							break;
						}
					}
					if (isWork) {
						ofs.add(ofsTemp.get(i));
					}
				}
				// tim so luong food them
				int foodPlus = 0;
				if (listDataVerify.size() > ofs.size() || listDataVerify.size() == ofs.size()) {
					foodPlus = listDataVerify.size() - ofs.size();
				}
				List<BaoChenhLech> baoChenhLechs = new ArrayList<>();
				// code new
				for (int i = 0; i < ofs.size(); i++) {

					// neu phan tu dau tien thi khong vao vong for
					boolean isFirst = false;
					if (baoChenhLechs.isEmpty()) {
						BaoChenhLech first = new BaoChenhLech();
						first.setNgay(DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date()));
						first.setCa(ofs.get(i).getFood_by_day().getShifts().getId());
						first.setTenmonan(ofs.get(i).getFood_by_day().getCategory_food().getName());
						first.setTenca(ofs.get(i).getFood_by_day().getShifts().getName());
						first.setSoluongchinhxac(1);
						first.setIdmonan(ofs.get(i).getFood_by_day().getCategory_food().getId());
						baoChenhLechs.add(first);
						// them mon tu chon vao danh sach
						// xem thu co mon tu chon do trong danh sach chua
						for (int k = 0; k < baoChenhLechs.size(); k++) {
							if (baoChenhLechs.get(k).getNgay().equals(ofs.get(i).getOrder_food().getRegistration_date())
									&& baoChenhLechs.get(k).getCa() == ofs.get(i).getFood_by_day().getShifts().getId()
									&& baoChenhLechs.get(k).getIdmonan() == ShiftsUtil.ID_MON_TU_CHON) {
								break;
							}
						}
						isFirst = true;
					}
					// khong phai lan dau tien moi vao
					if (!isFirst) {
						boolean isExist = false;
						int position = 0;
						for (int j = 0; j < baoChenhLechs.size(); j++) {
							// neu trung ngay, trung ca, trung ten mon se tang
							// so luong
							if (DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date())
									.equals(baoChenhLechs.get(j).getNgay())
									&& ofs.get(i).getFood_by_day().getShifts().getId() == baoChenhLechs.get(j).getCa()
									&& ofs.get(i).getFood_by_day().getCategory_food().getName()
											.equals(baoChenhLechs.get(j).getTenmonan())) {
								isExist = true;
								position = j;
								break;
							}
						}
						if (isExist) {
							baoChenhLechs.get(position)
									.setSoluongchinhxac(baoChenhLechs.get(position).getSoluongchinhxac() + 1);
						}

						else {
							BaoChenhLech temp = new BaoChenhLech();
							temp.setNgay(DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date()));
							temp.setCa(ofs.get(i).getFood_by_day().getShifts().getId());
							temp.setTenmonan(ofs.get(i).getFood_by_day().getCategory_food().getName());
							temp.setTenca(ofs.get(i).getFood_by_day().getShifts().getName());
							temp.setIdmonan(ofs.get(i).getFood_by_day().getCategory_food().getId());
							temp.setSoluongchinhxac(1);
							baoChenhLechs.add(temp);
							// them mon tu chon vao danh sach
							// xem thu co mon tu chon do trong danh sach chua
							for (int k = 0; k < baoChenhLechs.size(); k++) {
								if (baoChenhLechs.get(k).getNgay().equals(
										DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date()))
										&& baoChenhLechs.get(k).getCa() == ofs.get(i).getFood_by_day().getShifts()
												.getId()
										&& baoChenhLechs.get(k).getIdmonan() == ShiftsUtil.ID_MON_TU_CHON) {
									break;
								}
							}
						}
					}
				}

				// them danh sach com khach
				List<FoodCustomer> foodCustomers = FOOD_CUSTOMER_SERVICE.find(dateSearchExactly, shiftsExactly);

				// them mon tu chon vao list
				if (!foodCustomers.isEmpty()) {
					for (FoodCustomer f : foodCustomers) {
						// vi su dung java.util.date se khong trung ngay lay
						// duoi DB khong group duoc
						BaoChenhLech monankhach = new BaoChenhLech();
						monankhach.setNgay(DateUtil.DATE_WITHOUT_TIME(f.getQuantity_food().getFood_date()));
						monankhach.setCa(f.getQuantity_food().getShifts().getId());
						if (f.getFoodName() == null) {
							monankhach.setTenmonan("Suất ăn khách");
						} else {
							monankhach.setTenmonan(f.getFoodName());
						}
						// chua handle xong
						// monankhach.setIdmonan(f.getId());
						monankhach.setGia(f.getPrice());
						monankhach.setSoluongchinhxac(f.getQuantity());
						monankhach.setTenca(f.getQuantity_food().getShifts().getName());
						baoChenhLechs.add(monankhach);
					}
				}

				// them so luong mon tang ca vao list -> chi lay nhung nguoi da
				// duoc duyet
				List<FoodOverTime> foodOT = FOOD_OVER_TIME_SERVICE.findByDaDuyet(dateSearchExactly, shiftsExactly,
						null);
				// them mon tu chon vao list
				if (!foodOT.isEmpty()) {
					// vi su dung java.util.date se khong trung ngay lay duoi DB
					// khong group duoc
					BaoChenhLech monantangca = new BaoChenhLech();
					monantangca.setNgay(DateUtil.DATE_WITHOUT_TIME(foodOT.get(0).getOver_time().getFood_date()));
					monantangca.setCa(foodOT.get(0).getOver_time().getShifts().getId());
					monantangca.setTenmonan("Suất ăn tăng ca");
					// chua handle xong
					// monankhach.setIdmonan(f.getId());
					monantangca.setSoluongchinhxac(foodOT.size());
					monantangca.setTenca(foodOT.get(0).getOver_time().getShifts().getName());
					baoChenhLechs.add(monantangca);
				}
				// end code new
				// long endTimeAll = System.currentTimeMillis() - startTimeAll;
				// System.out.println("Thoi gian toan bo chuong trinh: " +
				// endTimeAll);
				if (!baoChenhLechs.isEmpty()) {
					// report
					String reportPath = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/reports/bcchinhxacsuatan.jasper");
					JRDataSource beanDataSource = new JRBeanCollectionDataSource(baoChenhLechs);
					Map<String, Object> importParam = new HashMap<String, Object>();
					String image = FacesContext.getCurrentInstance().getExternalContext()
							.getRealPath("/resources/gfx/lixco_logo.png");
					importParam.put("logo", image);
					// query du lieu de lay so luong
					// convert date to sql.date
					List<QuantityFood> quantityFoods = QUANTITY_FOOD_SERVICE.find(dateSearchExactly, shiftsExactly);
					if (!quantityFoods.isEmpty()) {
						int total = 0;
						total = quantityFoods.get(0).getTongsuatan() + quantityFoods.get(0).getTongsuatankhach()
								+ foodOT.size();
						importParam.put("total_food", total);
						importParam.put("total_chay", quantityFoods.get(0).getSuatanchay());
						importParam.put("total_man", quantityFoods.get(0).getSuatanman());
						importParam.put("total_khach", quantityFoods.get(0).getTongsuatankhach());
						importParam.put("food_plus", quantityFoods.get(0).getTongsuatandkthem());
					} else {
						importParam.put("total_food", 0);
						importParam.put("total_chay", 0);
						importParam.put("total_man", 0);
						importParam.put("food_plus", 0);
					}
					// toan bo
					importParam.put("shifts_name", shiftsName);
					JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, importParam, beanDataSource);
					FacesContext facesContext = FacesContext.getCurrentInstance();
					OutputStream outputStream;
					outputStream = facesContext.getExternalContext().getResponseOutputStream();
					JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
					facesContext.responseComplete();
				} else {
					Notification.NOTI_WARN("Không có dữ liệu");
				}
			} else {
				Notification.NOTI_WARN("Chưa có dữ liệu chính xác");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateSuatAnChinhXac() {
		// neu chua co thi them moi
		if (quantitySelected.getId() == null) {
			quantitySelected.setCreatedDate(new Date());
			quantitySelected.setCreatedUser(member.getCode());
			// quantitySelected.setFood_date(DateUtil.DATE_WITHOUT_TIME(quantitySelected.getFood_date()));
			QuantityFood qCheck = QUANTITY_FOOD_SERVICE.create(quantitySelected);
			if (qCheck != null) {
				// Notification.NOTI_SUCCESS("Thành công");
				MessageView.INFO("Thành công");
				return;
			} else {
				MessageView.ERROR("Không thành công");
				return;
			}
		}
		// da co -> cap nhat
		if (quantitySelected.getId() != null) {
			quantitySelected.setModifiedDate(new Date());
			quantitySelected.setModifiedUser(member.getCode());
			QuantityFood qCheck = QUANTITY_FOOD_SERVICE.update(quantitySelected);
			if (qCheck != null) {
				// Notification.NOTI_SUCCESS("Thành công");
				MessageView.INFO("Thành công");
				return;
			} else {
				Notification.NOTI_ERROR("Không thành công");
				return;
			}
		}
	}

	public void baoCaoTongHopKetQuaPDF() {
		try {
			// list de gan qua report
			List<FoodNhaAn> foods = FOOD_NHA_AN_SERVICE.findByDayToDaySortByDateAndShiftsAndFoodName(fromDateAte,
					toDateAte, null);
			if (!foods.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/bctonghopsuatan.jasper");
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// tong hop toan bo can bo CNV cong ty
	public void baoCaoTheoNgayExcel() throws IOException {
		List<FoodNhaAn> foods = new ArrayList<>();
		String nameSheet = "";
		Date dateSearchWithoutTime = DateUtil.DATE_WITHOUT_TIME(dateSearch);
		if (shifts == 0) {
			nameSheet = formatter.format(dateSearch);
			foods = FOOD_NHA_AN_SERVICE.findByDate(dateSearchWithoutTime, 0);
		}
		if (shifts == ShiftsUtil.SHIFTS1_ID) {
			nameSheet = ShiftsUtil.SHIFTS1_NAME + "-" + formatter.format(dateSearch);
			foods = FOOD_NHA_AN_SERVICE.findByDate(dateSearchWithoutTime, shifts);
		}
		if (shifts == ShiftsUtil.SHIFTS2_ID) {
			nameSheet = ShiftsUtil.SHIFTS2_NAME + "-" + formatter.format(dateSearch);
			foods = FOOD_NHA_AN_SERVICE.findByDate(dateSearchWithoutTime, shifts);
		}
		if (shifts == ShiftsUtil.SHIFTS3_ID) {
			nameSheet = ShiftsUtil.SHIFTS2_NAME + "-" + formatter.format(dateSearch);
			foods = FOOD_NHA_AN_SERVICE.findByDate(dateSearchWithoutTime, shifts);
		}
		// handle data
		// neu khong chon ky khao sat nao

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = null;
		sheet = workbook.createSheet(nameSheet);

		int rownum = 0;
		Cell cell;
		Row row;
		XSSFCellStyle style = createStyleForTitle(workbook);
		style.setAlignment(CellStyle.ALIGN_CENTER);

		// cell style for date
		XSSFCellStyle cellStyleDate = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

		XSSFCellStyle styleContent = workbook.createCellStyle();
		row = sheet.createRow(rownum);

		// EmpNo
		cell = row.createCell(0);
		cell.setCellValue("Mã NV");
		// xep loai// EmpName
		cell = row.createCell(1);
		cell.setCellValue("Tên NV");
		cell.setCellStyle(style);
		// Salary
		cell = row.createCell(2);
		cell.setCellValue("Phòng ban");
		cell.setCellStyle(style);

		cell = row.createCell(3);
		cell.setCellValue("Ngày");
		cell.setCellStyle(style);

		// Grade
		cell = row.createCell(4);
		cell.setCellValue("Ca làm việc");
		cell.setCellStyle(style);
		// Bonus
		cell = row.createCell(5);
		cell.setCellValue("Tên món");
		cell.setCellStyle(style);
		// xep loai

		// Data
		if (!foods.isEmpty()) {
			for (FoodNhaAn f : foods) {
				// Gson gson = new Gson();
				rownum++;
				row = sheet.createRow(rownum);

				// Tim nhan vien
				EmployeeDTO employeeTemp = EMPLOYEE_SERVICE_PUBLIC.findByCode(f.getEmployee_code());
				if (employeeTemp != null) {
					// ma nhan vien
					cell = row.createCell(0);
					cell.setCellValue(employeeTemp.getCode());
					// ten nhan vien
					cell = row.createCell(1);
					cell.setCellValue(employeeTemp.getName());
				}
				// phong
				Department departmentTemp = DEPARTMENT_SERVICE_PUBLIC.findByCode("code", f.getDepartment_code());
				if (departmentTemp != null) {
					// ten phong ban
					cell = row.createCell(2);
					cell.setCellValue(departmentTemp.getName());
				}
				// ngay
				cell = row.createCell(3);
				cell.setCellValue(f.getFood_date());
				cell.setCellStyle(cellStyleDate);
				// ten ca lam viec
				cell = row.createCell(4);
				cell.setCellValue(f.getShifts().getName());
				// ten mon an
				cell = row.createCell(5);
				cell.setCellValue(f.getCategory_food().getName());
			}
		}

		String filename = "baocao.xlsx";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/vnd.ms-excel");
		externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		workbook.write(externalContext.getResponseOutputStream());
		// cancel progress
		facesContext.responseComplete();
	}

	public void chiTietDuBaoSuatAnPDF() {
		try {
			// list de gan qua report
			List<OrderAndFoodByDate> ofs = ORDER_AND_FOOD_BY_DATE_SERVICE.findByDayToDaySortByDateAndShifts(fromDate,
					toDate, null);
			// test new
			List<BaoChenhLech> baoChenhLechs = new ArrayList<>();
			for (int i = 0; i < ofs.size(); i++) {
				// neu phan tu dau tien thi khong vao vong for
				boolean isFirst = false;
				if (baoChenhLechs.isEmpty()) {
					BaoChenhLech first = new BaoChenhLech();
					first.setNgay(DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date()));
					first.setCa(ofs.get(i).getFood_by_day().getShifts().getId());
					first.setTenmonan(ofs.get(i).getFood_by_day().getCategory_food().getName());
					first.setTenca(ofs.get(i).getFood_by_day().getShifts().getName());
					first.setSoluongchinhxac(1);
					first.setIdmonan(ofs.get(i).getFood_by_day().getCategory_food().getId());
					baoChenhLechs.add(first);
					isFirst = true;
				}
				// khong phai lan dau tien moi vao
				if (!isFirst) {
					boolean isExist = false;
					int position = 0;
					for (int j = 0; j < baoChenhLechs.size(); j++) {
						// neu trung ngay, trung ca, trung ten mon se tang so
						// luong
						if (DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date())
								.equals(baoChenhLechs.get(j).getNgay())
								&& ofs.get(i).getFood_by_day().getShifts().getId() == baoChenhLechs.get(j).getCa()
								&& ofs.get(i).getFood_by_day().getCategory_food().getName()
										.equals(baoChenhLechs.get(j).getTenmonan())) {
							isExist = true;
							position = j;
							break;
						}
					}
					if (isExist) {
						baoChenhLechs.get(position)
								.setSoluongchinhxac(baoChenhLechs.get(position).getSoluongchinhxac() + 1);
					}

					else {
						BaoChenhLech temp = new BaoChenhLech();
						temp.setNgay(DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date()));
						temp.setCa(ofs.get(i).getFood_by_day().getShifts().getId());
						temp.setTenmonan(ofs.get(i).getFood_by_day().getCategory_food().getName());
						temp.setTenca(ofs.get(i).getFood_by_day().getShifts().getName());
						temp.setIdmonan(ofs.get(i).getFood_by_day().getCategory_food().getId());
						temp.setSoluongchinhxac(1);
						baoChenhLechs.add(temp);
					}
				}
			}

			// de hien thi ngay cuoi cung hoac chon 1 ngay
			toDate = DateUtil.addDays(toDate, 1);

			// them mon tu chon vao list
			for (Date date = fromDate; date.before(toDate); date = DateUtil.addDays(date, 1)) {
				// tim so luong dki them
				Date dateCurr = DateUtil.DATE_WITHOUT_TIME(date);
				List<OrderAndFoodByDate> odfbds = ORDER_AND_FOOD_BY_DATE_SERVICE.find(dateCurr, ShiftsUtil.SHIFTS1_ID,
						null);
				List<ReportFoodByDay> reportFoods = REPORT_FOOD_BY_DAY_SERVICE.findByDate(dateCurr,
						ShiftsUtil.SHIFTS1_ID);
				// end
				// vi su dung java.util.date se khong trung ngay lay duoi DB
				// khong group duoc
				BaoChenhLech montuchonCa1 = new BaoChenhLech();
				montuchonCa1.setNgay(dateCurr);
				montuchonCa1.setCa(ShiftsUtil.SHIFTS1_ID);
				montuchonCa1.setTenmonan(ShiftsUtil.TEN_MON_TU_CHON);
				montuchonCa1.setIdmonan(ShiftsUtil.ID_MON_TU_CHON);
				montuchonCa1.setTenca(ShiftsUtil.SHIFTS1_NAME);
				montuchonCa1.setSoluongchinhxac(0);
				// tim so luong mon tu chon

				// tim so luong dki them
				List<ReportFoodByDay> reportFoods2 = REPORT_FOOD_BY_DAY_SERVICE.findByDate(dateCurr,
						ShiftsUtil.SHIFTS2_ID);
				// end
				BaoChenhLech montuchonCa2 = new BaoChenhLech();
				montuchonCa2.setNgay(dateCurr);
				montuchonCa2.setCa(ShiftsUtil.SHIFTS2_ID);
				montuchonCa2.setTenmonan(ShiftsUtil.TEN_MON_TU_CHON);
				montuchonCa2.setIdmonan(ShiftsUtil.ID_MON_TU_CHON);
				montuchonCa2.setTenca(ShiftsUtil.SHIFTS2_NAME);
				montuchonCa2.setSoluongchinhxac(0);

				// tim so luong dki them
				List<ReportFoodByDay> reportFoods3 = REPORT_FOOD_BY_DAY_SERVICE.findByDate(dateCurr,
						ShiftsUtil.SHIFTS3_ID);
				BaoChenhLech montuchonCa3 = new BaoChenhLech();
				montuchonCa3.setNgay(dateCurr);
				montuchonCa3.setCa(ShiftsUtil.SHIFTS3_ID);
				montuchonCa3.setTenmonan(ShiftsUtil.TEN_MON_TU_CHON);
				montuchonCa3.setIdmonan(ShiftsUtil.ID_MON_TU_CHON);
				montuchonCa3.setTenca(ShiftsUtil.SHIFTS3_NAME);
				montuchonCa3.setSoluongchinhxac(0);

				//
				// tong so luong mon da dang ki
				int tongsoluongdk1 = 0;
				int tongsoluongdk2 = 0;
				int tongsoluongdk3 = 0;
				for (BaoChenhLech b : baoChenhLechs) {
					if (!reportFoods.isEmpty()) {
						if (b.getNgay().equals(reportFoods.get(0).getReport_date())) {
							if (b.getCa() == ShiftsUtil.SHIFTS1_ID) {
								tongsoluongdk1 = tongsoluongdk1 + b.getSoluongchinhxac();
							}
							if (b.getCa() == ShiftsUtil.SHIFTS2_ID) {
								tongsoluongdk2 = tongsoluongdk2 + b.getSoluongchinhxac();
							}
							if (b.getCa() == ShiftsUtil.SHIFTS3_ID) {
								tongsoluongdk3 = tongsoluongdk3 + b.getSoluongchinhxac();
							}
						}
					}
				}
				if (!reportFoods.isEmpty()) {
					montuchonCa1.setSoluongchinhxac(reportFoods.get(0).getQuantity() - tongsoluongdk1);
				}
				if (!reportFoods2.isEmpty()) {
					montuchonCa2.setSoluongchinhxac(reportFoods2.get(0).getQuantity() - tongsoluongdk2);
				}
				if (!reportFoods3.isEmpty()) {
					montuchonCa3.setSoluongchinhxac(reportFoods3.get(0).getQuantity() - tongsoluongdk3);
				}
				baoChenhLechs.add(montuchonCa1);
				baoChenhLechs.add(montuchonCa2);
				baoChenhLechs.add(montuchonCa3);
			}
			sortByDateAndFoodNameBaoChenhLech(baoChenhLechs);
			// end test new

			if (!baoChenhLechs.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/dubaochitietsuatan.jasper");
				JRDataSource beanDataSource = new JRBeanCollectionDataSource(baoChenhLechs);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void baoCaoTongHopDuBaoSuatAnPDF() {
		try {
			// list de gan qua report
			List<OrderAndFoodByDate> ofs = ORDER_AND_FOOD_BY_DATE_SERVICE
					.findByDayToDaySortByDateAndFoodName(fromDateDetail, toDateDetail, null);

			// CODE NEW
			// test new
			List<BaoChenhLech> baoChenhLechs = new ArrayList<>();
			for (int i = 0; i < ofs.size(); i++) {
				// neu phan tu dau tien thi khong vao vong for
				boolean isFirst = false;
				if (baoChenhLechs.isEmpty()) {
					BaoChenhLech first = new BaoChenhLech();
					first.setNgay(DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date()));
					first.setTenmonan(ofs.get(i).getFood_by_day().getCategory_food().getName());
					first.setSoluongchinhxac(1);
					first.setIdmonan(ofs.get(i).getFood_by_day().getCategory_food().getId());
					baoChenhLechs.add(first);
					isFirst = true;
				}
				// khong phai lan dau tien moi vao
				if (!isFirst) {
					boolean isExist = false;
					int position = 0;
					for (int j = 0; j < baoChenhLechs.size(); j++) {
						// neu trung ngay, trung ten mon se tang so luong
						if (DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date())
								.equals(baoChenhLechs.get(j).getNgay())
								&& ofs.get(i).getFood_by_day().getCategory_food().getName()
										.equals(baoChenhLechs.get(j).getTenmonan())) {
							isExist = true;
							position = j;
							break;
						}
					}
					if (isExist) {
						baoChenhLechs.get(position)
								.setSoluongchinhxac(baoChenhLechs.get(position).getSoluongchinhxac() + 1);
					}

					else {
						BaoChenhLech temp = new BaoChenhLech();
						temp.setNgay(DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date()));
						temp.setTenmonan(ofs.get(i).getFood_by_day().getCategory_food().getName());
						temp.setIdmonan(ofs.get(i).getFood_by_day().getCategory_food().getId());
						temp.setSoluongchinhxac(1);
						baoChenhLechs.add(temp);
					}
				}
			}
			// de hien thi ngay cuoi cung hoac chon 1 ngay
			toDateDetail = DateUtil.addDays(toDateDetail, 1);
			// them mon tu chon vao list
			for (Date date = fromDateDetail; date.before(toDateDetail); date = DateUtil.addDays(date, 1)) {
				// tim so luong dki them
				Date dateCurr = DateUtil.DATE_WITHOUT_TIME(date);
				List<ReportFoodByDay> reportFoods = REPORT_FOOD_BY_DAY_SERVICE.findByDate(dateCurr, 0);
				int tongslmonantuchon = 0;
				if (reportFoods.size() != 0) {
					for (ReportFoodByDay r : reportFoods) {
						tongslmonantuchon = tongslmonantuchon + r.getQuantity();
					}
				}

				// end
				// vi su dung java.util.date se khong trung ngay lay duoi DB
				// khong group duoc
				BaoChenhLech montuchonCa1 = new BaoChenhLech();
				montuchonCa1.setNgay(dateCurr);
				montuchonCa1.setTenmonan(ShiftsUtil.TEN_MON_TU_CHON);
				montuchonCa1.setIdmonan(ShiftsUtil.ID_MON_TU_CHON);
				montuchonCa1.setSoluongchinhxac(0);
				// tim so luong mon tu chon

				// tong so luong mon da dang ki
				int tongsoluongdk = 0;

				for (BaoChenhLech b : baoChenhLechs) {
					if (!reportFoods.isEmpty()) {
						if (b.getNgay().equals(reportFoods.get(0).getReport_date())) {
							tongsoluongdk = tongsoluongdk + b.getSoluongchinhxac();
						}
					}
				}
				montuchonCa1.setSoluongchinhxac(tongslmonantuchon - tongsoluongdk);
				baoChenhLechs.add(montuchonCa1);
			}

			sortByDateAndFoodNameBaoChenhLech(baoChenhLechs);
			// end test new
			// END CODE NEW
			if (!baoChenhLechs.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/bctonghopdubaosuatan.jasper");
				JRDataSource beanDataSource = new JRBeanCollectionDataSource(baoChenhLechs);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// bao cao chi tiet excel
	public void bcTongHopDuBaoSuatAnExcel() throws IOException {
		List<OrderAndFoodByDate> ofs = ORDER_AND_FOOD_BY_DATE_SERVICE
				.findByDayToDaySortByDateAndFoodName(fromDateDetail, toDateDetail, null);
		List<ChiTietDuBaoSuatAn> chitietdubaosuatans = new ArrayList<>();
		if (!ofs.isEmpty()) {
			for (int i = 0; i < ofs.size(); i++) {
				if (chitietdubaosuatans.isEmpty()) {
					ChiTietDuBaoSuatAn temp = new ChiTietDuBaoSuatAn();
					temp.setId(ofs.get(i).getId());
					temp.setNgay(ofs.get(i).getOrder_food().getRegistration_date());
					temp.setSoluong(1);
					temp.setTenmon(ofs.get(i).getFood_by_day().getCategory_food().getName());
					chitietdubaosuatans.add(temp);
				} else {
					boolean checkExist = false;
					int vitri = 0;
					for (int j = 0; j < chitietdubaosuatans.size(); j++) {

						if (ofs.get(i).getOrder_food().getRegistration_date()
								.equals(chitietdubaosuatans.get(j).getNgay())
								&& ofs.get(i).getFood_by_day().getCategory_food().getName()
										.equals(chitietdubaosuatans.get(j).getTenmon())) {
							checkExist = true;
							vitri = j;
							break;
						}
					}
					if (checkExist) {
						chitietdubaosuatans.get(vitri).setSoluong(chitietdubaosuatans.get(vitri).getSoluong() + 1);
					} else {
						ChiTietDuBaoSuatAn temp1 = new ChiTietDuBaoSuatAn();
						temp1.setId(ofs.get(i).getId());
						temp1.setNgay(ofs.get(i).getOrder_food().getRegistration_date());
						temp1.setSoluong(1);
						temp1.setTenmon(ofs.get(i).getFood_by_day().getCategory_food().getName());
						chitietdubaosuatans.add(temp1);
					}
				}
			}
		}

		String nameSheet = "";
		nameSheet = "BC TỔNG HỢP DỰ BÁO SUẤT ĂN";
		// handle data
		// neu khong chon ky khao sat nao

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = null;
		sheet = workbook.createSheet(nameSheet);

		int rownum = 0;
		Cell cell;
		Row row;
		XSSFCellStyle style = createStyleForTitle(workbook);
		style.setAlignment(CellStyle.ALIGN_CENTER);

		// cell style for date
		XSSFCellStyle cellStyleDate = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

		row = sheet.createRow(rownum);

		// EmpNo
		cell = row.createCell(0);
		cell.setCellValue("Ngày");
		// xep loai// EmpName
		cell = row.createCell(1);
		cell.setCellValue("Tên món ăn");
		cell.setCellStyle(style);
		// Salary
		cell = row.createCell(2);
		cell.setCellValue("Số lượng");
		cell.setCellStyle(style);

		// Data
		if (!chitietdubaosuatans.isEmpty()) {
			for (ChiTietDuBaoSuatAn c : chitietdubaosuatans) {
				rownum++;
				row = sheet.createRow(rownum);
				// ngay
				cell = row.createCell(0);
				cell.setCellValue(c.getNgay());
				cell.setCellStyle(cellStyleDate);
				// ten ca lam viec
				cell = row.createCell(1);
				cell.setCellValue(c.getTenmon());
				// ten mon an
				cell = row.createCell(2);
				cell.setCellValue(c.getSoluong());
			}
		}

		String filename = "baocaotonghop.xlsx";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/vnd.ms-excel");
		externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		workbook.write(externalContext.getResponseOutputStream());
		// cancel progress
		facesContext.responseComplete();
	}

	private List<OrderFoodReport> sapxep(List<OrderFoodReport> items) {
		try {
			Collections.sort(items, new Comparator<OrderFoodReport>() {
				@Override
				public int compare(OrderFoodReport sv1, OrderFoodReport sv2) {
					try {
						boolean ngay = sv1.getRegistrationDate().equals(sv2.getRegistrationDate());
						if (ngay) {
							if (sv1.getShift() > sv2.getShift()) {
								return 1;
							} else if (sv1.getShift() == sv2.getShift()) {
								return sv1.getFoodName().compareTo(sv2.getFoodName());
							} else {
								return -1;
							}
						} else {
							boolean ngayss = sv1.getRegistrationDate().before(sv2.getRegistrationDate());
							if (ngayss)
								return -1;
							return 1;
						}
					} catch (Exception e) {
						return -1;
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

	private List<OrderFoodReport> sortByDateAndFoodName(List<OrderFoodReport> items) {
		try {
			Collections.sort(items, new Comparator<OrderFoodReport>() {
				@Override
				public int compare(OrderFoodReport sv1, OrderFoodReport sv2) {
					try {
						boolean ngay = sv1.getRegistrationDate().equals(sv2.getRegistrationDate());
						if (ngay) {
							return sv1.getFoodName().compareTo(sv2.getFoodName());
						} else {
							boolean ngayss = sv1.getRegistrationDate().before(sv2.getRegistrationDate());
							if (ngayss)
								return -1;
							return 1;
						}
					} catch (Exception e) {
						return -1;
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

	public void baoCaoChenhLech() {
		try {
			// check co di lam hay khong
			DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
			String dateSearchString = formatter1.format(fromDateAte);
			TimekeepingData[] arr = null;
			// tim ca lam viec
			String shiftsName = "";
			if (shiftsSelected.getId() == ShiftsUtil.SHIFTS1_ID) {
				shiftsName = ShiftsUtil.SHIFTS1_NAME;
				// 8C la ca sang 6h->6h toi // gom nv van phong va cong nhan
				arr = TimekeepingDataService.timtheongay(dateSearchString);
			}
			if (shiftsSelected.getId() == ShiftsUtil.SHIFTS2_ID) {
				shiftsName = ShiftsUtil.SHIFTS2_NAME;
				// nhan vien di ca
				arr = TimekeepingDataService.searchByDateAndWorkTemp(dateSearchString, "8C");
				List<TimekeepingData> arrListTemp = new ArrayList<>();
				if (arr != null) {
					for (int i = 0; i < arr.length; i++) {
						// check dieu kien co di ca hay khong
						if (arr[i].isWorkShift()) {
							arrListTemp.add(arr[i]);
						}
					}
					arr = arrListTemp.toArray(new TimekeepingData[arrListTemp.size()]);
				}
			}
			if (shiftsSelected.getId() == ShiftsUtil.SHIFTS3_ID) {
				shiftsName = ShiftsUtil.SHIFTS3_NAME;
				// cd la ca chieu 6h toi-> 6h sang
				arr = TimekeepingDataService.searchByDateAndWorkTemp(dateSearchString, "CD");
			}
			// convert array to list
			List<TimekeepingData> listDataVerify = new ArrayList<>();
			if (arr != null) {
				listDataVerify = Arrays.asList(arr);
			}
			// list nhan vien di lam
			List<OrderAndFoodByDate> ofs = new ArrayList<>();
			if (!listDataVerify.isEmpty()) {
				List<OrderAndFoodByDate> ofsTemp = ORDER_AND_FOOD_BY_DATE_SERVICE.findByDate(fromDateAte,
						shiftsSelected.getId());
				// kiem tra nv hom do co di lam khong
				for (int i = 0; i < ofsTemp.size(); i++) {
					boolean isWork = false;
					for (int j = 0; j < listDataVerify.size(); j++) {
						if (ofsTemp.get(i).getOrder_food().getEmployee_id()
								.equals(listDataVerify.get(j).getCodeOld())) {
							isWork = true;
							break;
						}
					}
					if (isWork) {
						ofs.add(ofsTemp.get(i));
					}
				}
			}
			// end check
			List<BaoChenhLech> baoChenhLechs = new ArrayList<>();
			for (int i = 0; i < ofs.size(); i++) {
				// neu phan tu dau tien thi khong vao vong for
				boolean isFirst = false;
				if (baoChenhLechs.isEmpty()) {
					BaoChenhLech first = new BaoChenhLech();
					first.setNgay(DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date()));
					first.setCa(ofs.get(i).getFood_by_day().getShifts().getId());
					first.setTenmonan(ofs.get(i).getFood_by_day().getCategory_food().getName());
					first.setTenca(ofs.get(i).getFood_by_day().getShifts().getName());
					first.setSoluongchinhxac(1);
					first.setIdmonan(ofs.get(i).getFood_by_day().getCategory_food().getId());
					baoChenhLechs.add(first);
					// them mon tu chon vao danh sach
					// xem thu co mon tu chon do trong danh sach chua
					for (int k = 0; k < baoChenhLechs.size(); k++) {
						if (baoChenhLechs.get(k).getNgay()
								.equals(DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date()))
								&& baoChenhLechs.get(k).getCa() == ofs.get(i).getFood_by_day().getShifts().getId()
								&& baoChenhLechs.get(k).getIdmonan() == ShiftsUtil.ID_MON_TU_CHON) {
							break;
						}
					}
					isFirst = true;
				}
				// khong phai lan dau tien moi vao
				if (!isFirst) {
					boolean isExist = false;
					int position = 0;
					for (int j = 0; j < baoChenhLechs.size(); j++) {
						// neu trung ngay, trung ca, trung ten mon se tang so
						// luong
						if (DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date())
								.equals(baoChenhLechs.get(j).getNgay())
								&& ofs.get(i).getFood_by_day().getShifts().getId() == baoChenhLechs.get(j).getCa()
								&& ofs.get(i).getFood_by_day().getCategory_food().getName()
										.equals(baoChenhLechs.get(j).getTenmonan())) {
							isExist = true;
							position = j;
							break;
						}
					}
					if (isExist) {
						baoChenhLechs.get(position)
								.setSoluongchinhxac(baoChenhLechs.get(position).getSoluongchinhxac() + 1);
					}

					else {
						BaoChenhLech temp = new BaoChenhLech();
						temp.setNgay(DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date()));
						temp.setCa(ofs.get(i).getFood_by_day().getShifts().getId());
						temp.setTenmonan(ofs.get(i).getFood_by_day().getCategory_food().getName());
						temp.setTenca(ofs.get(i).getFood_by_day().getShifts().getName());
						temp.setIdmonan(ofs.get(i).getFood_by_day().getCategory_food().getId());
						temp.setSoluongchinhxac(1);
						baoChenhLechs.add(temp);
						// them mon tu chon vao danh sach
						// xem thu co mon tu chon do trong danh sach chua
						for (int k = 0; k < baoChenhLechs.size(); k++) {
							if (baoChenhLechs.get(k).getNgay().equals(
									DateUtil.DATE_WITHOUT_TIME(ofs.get(i).getOrder_food().getRegistration_date()))
									&& baoChenhLechs.get(k).getCa() == ofs.get(i).getFood_by_day().getShifts().getId()
									&& baoChenhLechs.get(k).getIdmonan() == ShiftsUtil.ID_MON_TU_CHON) {
								break;
							}
						}
					}
				}
			}

			// them mon tu chon vao list
			// group duoc
			BaoChenhLech montuchonCa1 = new BaoChenhLech();
			montuchonCa1.setNgay(DateUtil.DATE_WITHOUT_TIME(fromDateAte));
			montuchonCa1.setCa(shiftsSelected.getId());
			montuchonCa1.setTenmonan(ShiftsUtil.TEN_MON_TU_CHON);
			montuchonCa1.setIdmonan(ShiftsUtil.ID_MON_TU_CHON);
			montuchonCa1.setTenca(shiftsSelected.getName());
			montuchonCa1.setSoluongchinhxac(0);
			baoChenhLechs.add(montuchonCa1);

			// xu ly com khach
			BaoChenhLech montuchonComkhach = new BaoChenhLech();
			montuchonComkhach.setNgay(DateUtil.DATE_WITHOUT_TIME(fromDateAte));
			montuchonComkhach.setCa(shiftsSelected.getId());
			montuchonComkhach.setTenmonan("Suất ăn khách");
			montuchonComkhach.setIdmonan(0);
			montuchonComkhach.setTenca(shiftsSelected.getName());
			montuchonComkhach.setSoluongchinhxac(0);
			baoChenhLechs.add(montuchonComkhach);

			// Them suat an tang ca 1
			// them so luong mon tang ca vao list -> chi lay nhung nguoi da duoc
			// duyet
			List<FoodOverTime> foodOT = FOOD_OVER_TIME_SERVICE.findByDaDuyet(fromDateAte, shiftsSelected.getId(), null);
			if (!foodOT.isEmpty()) {
				// vi su dung java.util.date se khong trung ngay lay duoi DB
				// khong group duoc
				BaoChenhLech monantangca = new BaoChenhLech();
				monantangca.setNgay(DateUtil.DATE_WITHOUT_TIME(foodOT.get(0).getOver_time().getFood_date()));
				monantangca.setCa(foodOT.get(0).getOver_time().getShifts().getId());
				monantangca.setTenmonan("Suất ăn tăng ca");
				monantangca.setSoluongchinhxac(foodOT.size());
				monantangca.setTenca(foodOT.get(0).getOver_time().getShifts().getName());
				baoChenhLechs.add(monantangca);
			}
			// End them suat an tang ca

			// Gan so luong chinh xac cho mon tu chon
			for (int i = 0; i < baoChenhLechs.size(); i++) {
				// neu la mon an tu chon thi se set so luong
				List<QuantityFood> qfs = QUANTITY_FOOD_SERVICE
						.find(DateUtil.DATE_WITHOUT_TIME(baoChenhLechs.get(i).getNgay()), baoChenhLechs.get(i).getCa());
				if (baoChenhLechs.get(i).getTenmonan().equals(ShiftsUtil.TEN_MON_TU_CHON)) {
					// tim so luong chinh xac
					if (!qfs.isEmpty()) {
						baoChenhLechs.get(i).setSoluongchinhxac(qfs.get(0).getSuatanman() + qfs.get(0).getSuatanchay());
					}
					// tim so luong thuc te
					List<FoodNhaAn> fnas = FOOD_NHA_AN_SERVICE.find(
							DateUtil.DATE_WITHOUT_TIME(baoChenhLechs.get(i).getNgay()), baoChenhLechs.get(i).getCa(),
							baoChenhLechs.get(i).getIdmonan());
					baoChenhLechs.get(i).setSoluongthucte(fnas.size());
				}
				// suat an khach
				if (baoChenhLechs.get(i).getTenmonan().equals("Suất ăn khách")) {
					// so luong chinh xac
					if (!qfs.isEmpty()) {
						baoChenhLechs.get(i).setSoluongchinhxac(qfs.get(0).getTongsuatankhach());
						baoChenhLechs.get(i).setSoluongthucte(qfs.get(0).getTongsuatankhach());
					}
				}
				// khong phai mon tu chon
				if (!baoChenhLechs.get(i).getTenmonan().equals("Suất ăn khách")
						&& !baoChenhLechs.get(i).getTenmonan().equals(ShiftsUtil.TEN_MON_TU_CHON)) {
					// khong phai com khach
					if (baoChenhLechs.get(i).getIdmonan() != 0) {
						List<FoodNhaAn> fnas = FOOD_NHA_AN_SERVICE.find(baoChenhLechs.get(i).getNgay(),
								baoChenhLechs.get(i).getCa(), baoChenhLechs.get(i).getIdmonan());
						baoChenhLechs.get(i).setSoluongthucte(fnas.size());
					}
				}
			}
			sortByDateAndFoodNameBaoChenhLech(baoChenhLechs);
			if (!baoChenhLechs.isEmpty()) {
				// report
				String reportPath = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/reports/bcsochenhlech.jasper");
				JRDataSource beanDataSource = new JRBeanCollectionDataSource(baoChenhLechs);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<BaoChenhLech> sortByDateAndFoodNameBaoChenhLech(List<BaoChenhLech> items) {
		try {
			Collections.sort(items, new Comparator<BaoChenhLech>() {
				@Override
				public int compare(BaoChenhLech sv1, BaoChenhLech sv2) {
					try {
						boolean ngay = sv1.getNgay().equals(sv2.getNgay());
						if (ngay) {
							if (sv1.getCa() > sv2.getCa()) {
								return 1;
							} else if (sv1.getCa() == sv2.getCa()) {
								return sv1.getTenmonan().compareTo(sv2.getTenmonan());
							} else {
								return -1;
							}

						} else {
							boolean ngayss = sv1.getNgay().before(sv2.getNgay());
							if (ngayss)
								return -1;
							return 1;
						}
					} catch (Exception e) {
						return -1;
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

	@Override
	protected Logger getLogger() {
		return null;
	}

	public List<FoodNhaAn> getFoodNhaAns() {
		return foodNhaAns;
	}

	public void setFoodNhaAns(List<FoodNhaAn> foodNhaAns) {
		this.foodNhaAns = foodNhaAns;
	}

	public int getShifts() {
		return shifts;
	}

	public void setShifts(int shifts) {
		this.shifts = shifts;
	}

	public Date getDateSearch() {
		return dateSearch;
	}

	public void setDateSearch(Date dateSearch) {
		this.dateSearch = dateSearch;
	}

	public String getLoaiBaoCao() {
		return loaiBaoCao;
	}

	public void setLoaiBaoCao(String loaiBaoCao) {
		this.loaiBaoCao = loaiBaoCao;
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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getFromDateDetail() {
		return fromDateDetail;
	}

	public void setFromDateDetail(Date fromDateDetail) {
		this.fromDateDetail = fromDateDetail;
	}

	public Date getToDateDetail() {
		return toDateDetail;
	}

	public void setToDateDetail(Date toDateDetail) {
		this.toDateDetail = toDateDetail;
	}

	public boolean[] getCheckedRenderView() {
		return checkedRenderView;
	}

	public void setCheckedRenderView(boolean[] checkedRenderView) {
		this.checkedRenderView = checkedRenderView;
	}

	public int getValueChecked() {
		return valueChecked;
	}

	public void setValueChecked(int valueChecked) {
		this.valueChecked = valueChecked;
	}

	public Date getFromDateAte() {
		return fromDateAte;
	}

	public void setFromDateAte(Date fromDateAte) {
		this.fromDateAte = fromDateAte;
	}

	public Date getToDateAte() {
		return toDateAte;
	}

	public void setToDateAte(Date toDateAte) {
		this.toDateAte = toDateAte;
	}

	public int getShiftsExactly() {
		return shiftsExactly;
	}

	public void setShiftsExactly(int shiftsExactly) {
		this.shiftsExactly = shiftsExactly;
	}

	public Date getDateSearchExactly() {
		return dateSearchExactly;
	}

	public void setDateSearchExactly(Date dateSearchExactly) {
		this.dateSearchExactly = dateSearchExactly;
	}

	public int getTongSoLuong() {
		return tongSoLuong;
	}

	public void setTongSoLuong(int tongSoLuong) {
		this.tongSoLuong = tongSoLuong;
	}

	public int getSoLuongDangKy() {
		return soLuongDangKy;
	}

	public void setSoLuongDangKy(int soLuongDangKy) {
		this.soLuongDangKy = soLuongDangKy;
	}

	public int getSoLuongDangKyThem() {
		return soLuongDangKyThem;
	}

	public void setSoLuongDangKyThem(int soLuongDangKyThem) {
		this.soLuongDangKyThem = soLuongDangKyThem;
	}

	public int getSoLuongMonChay() {
		return soLuongMonChay;
	}

	public void setSoLuongMonChay(int soLuongMonChay) {
		this.soLuongMonChay = soLuongMonChay;
	}

	public int getSoLuongMonMan() {
		return soLuongMonMan;
	}

	public void setSoLuongMonMan(int soLuongMonMan) {
		this.soLuongMonMan = soLuongMonMan;
	}

	public QuantityFood getQuantitySelected() {
		return quantitySelected;
	}

	public void setQuantitySelected(QuantityFood quantitySelected) {
		this.quantitySelected = quantitySelected;
	}

	public List<FoodCustomer> getFoodCustomersByQuantityFood() {
		return foodCustomersByQuantityFood;
	}

	public void setFoodCustomersByQuantityFood(List<FoodCustomer> foodCustomersByQuantityFood) {
		this.foodCustomersByQuantityFood = foodCustomersByQuantityFood;
	}

	public int getTotalTangCa() {
		return totalTangCa;
	}

	public void setTotalTangCa(int totalTangCa) {
		this.totalTangCa = totalTangCa;
	}

	public List<Shifts> getAllShifts() {
		return allShifts;
	}

	public void setAllShifts(List<Shifts> allShifts) {
		this.allShifts = allShifts;
	}

	public Shifts getShiftsSelected() {
		return shiftsSelected;
	}

	public void setShiftsSelected(Shifts shiftsSelected) {
		this.shiftsSelected = shiftsSelected;
	}
}

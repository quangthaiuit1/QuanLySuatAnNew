package trong.lixco.com.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.account.servicepublics.Member;
import trong.lixco.com.account.servicepublics.MemberServicePublic;
import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
import trong.lixco.com.bean.entities.EmployeeThai;
import trong.lixco.com.bean.staticentity.DateUtil;
import trong.lixco.com.bean.staticentity.MessageView;
import trong.lixco.com.bean.staticentity.Notification;
import trong.lixco.com.ejb.service.FoodOverTimeService;
import trong.lixco.com.ejb.service.OverTimeService;
import trong.lixco.com.ejb.service.ShiftsService;
import trong.lixco.com.ejb.service.TimeBoundService;
import trong.lixco.com.jpa.entity.FoodOverTime;
import trong.lixco.com.jpa.entity.OrderFood;
import trong.lixco.com.jpa.entity.OverTime;
import trong.lixco.com.jpa.entity.Shifts;
import trong.lixco.com.jpa.entity.TimeBound;
import trong.lixco.com.servicepublic.EmployeeDTO;
import trong.lixco.com.servicepublic.EmployeeServicePublic;
import trong.lixco.com.servicepublic.EmployeeServicePublicProxy;
import trong.lixco.com.util.DepartmentUtil;

@Named
@ViewScoped
public class SuatAnTangCaBean extends AbstractBean<OrderFood> {
	private static final long serialVersionUID = 1L;
	private Department departmentSearch;
	private List<Department> departmentSearchs;
	private List<EmployeeDTO> employeesByDepartment;
	private Date dateSearch;
	private int Shifts = 1;
	private Shifts shiftsSelected;
	private List<Shifts> allShifts;
	private Member member;
	private List<Member> members;
	private boolean daDuyetSuatAnView = false;
	private List<EmployeeThai> employeesThai;
	private List<EmployeeThai> employeesThaiFilters;

	DepartmentServicePublic departmentServicePublic;
	MemberServicePublic memberServicePublic;
	EmployeeServicePublic EMPLOYEE_SERVICE_PUBLIC;

	@Inject
	private ShiftsService SHIFTS_SERVICE;
	@Inject
	private FoodOverTimeService FOOD_OVER_TIME_SERVICE;
	@Inject
	private OverTimeService OVER_TIME_SERVICE;
	@Inject
	private TimeBoundService TIME_BOUND_SERVICE;

	@Override
	protected void initItem() {
		// dateSearch = new Date();
		dateSearch = DateUtil.DATE_WITHOUT_TIME(new Date());
		employeesThai = new ArrayList<>();
		try {
			departmentServicePublic = new DepartmentServicePublicProxy();
			memberServicePublic = new MemberServicePublicProxy();
			EMPLOYEE_SERVICE_PUBLIC = new EmployeeServicePublicProxy();
			departmentSearchs = new ArrayList<Department>();
			member = getAccount().getMember();
			// handle cho chi oanh tim duoc tat ca cac phong ban
			if (getAccount().isAdmin() || getAccount().getMember().getCode().equals("0001803")) {
				Department[] deps = departmentServicePublic.findAll();
				for (int i = 0; i < deps.length; i++) {
					if (deps[i].getLevelDep() != null)
						if (deps[i].getLevelDep().getLevel() > 1)
							departmentSearchs.add(deps[i]);
				}

			} else {
				departmentSearchs.add(member.getDepartment());
			}
			if (departmentSearchs.size() != 0) {
				departmentSearchs = DepartmentUtil.sort(departmentSearchs);
				departmentSearch = departmentSearchs.get(0);
			}
			// handle shifts
			allShifts = SHIFTS_SERVICE.findAll();
			if (allShifts.size() != 0) {
				shiftsSelected = allShifts.get(0);
			}
			searchItem();

		} catch (Exception e) {
		}

	}

	public void searchItem() {
		try {
			// daDuyet = false;
			daDuyetSuatAnView = false;
			employeesThai = new ArrayList<>();
			// handle nhan vien da chon
			List<FoodOverTime> foodOT = FOOD_OVER_TIME_SERVICE.find(dateSearch, shiftsSelected.getId(),
					departmentSearch.getCode());
			// check duyet hay chua
			if (foodOT.size() != 0) {
				if (foodOT.get(0).getOver_time().isIs_duyet()) {
					daDuyetSuatAnView = true;
					// daDuyet = true;
				}
			}
			// handle hien toan bo danh sach nhan vien
			List<String> departmentsString = new ArrayList<>();
			departmentsString.add(departmentSearch.getCode());
			String[] departmentsStringArr = departmentsString.toArray(new String[departmentsString.size()]);
			EmployeeDTO[] employeesArray = EMPLOYEE_SERVICE_PUBLIC.findByDep(departmentsStringArr);
			if (employeesArray != null) {
				for (int i = 0; i < employeesArray.length; i++) {
					EmployeeThai employeeTemp = new EmployeeThai();
					employeeTemp.setEmployeeCode(employeesArray[i].getCode());
					employeeTemp.setEmployeeName(employeesArray[i].getName());
					employeeTemp.setDepartmentCode(employeesArray[i].getCodeDepart());
					employeeTemp.setDepartmentName(employeesArray[i].getNameDepart());
					if (employeesArray[i].getCodeOld() != null) {
						employeeTemp.setEmployeeCodeOld(employeesArray[i].getCodeOld());
					}
					employeesThai.add(employeeTemp);
				}
				if (employeesThai.size() != 0) {
					for (int i = 0; i < foodOT.size(); i++) {
						for (int j = 0; j < employeesThai.size(); j++) {
							if (foodOT.get(i).getEmployee_code().equals(employeesThai.get(j).getEmployeeCode())) {
								employeesThai.get(j).setSelect(true);
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveOrUpdate() {
		// chua duyet
		boolean isCurrent = false;
		// query gio duoi DB
		TimeBound timeDKTC = TIME_BOUND_SERVICE.find("DKTC");
		// int minutes = Integer.parseInt(timeDKTC.getMinutes());
		String timeDKSAString = timeDKTC.getHour() + ":" + timeDKTC.getMinutes();
		try {
			isCurrent = isCurrentDate(dateSearch, timeDKSAString);
		} catch (Exception e) {
		}
		if (isCurrent) {
			// Notification.NOTI_WARN("Vui lòng đăng ký trước 15h ngày hôm
			// trước");
			MessageView.WARN("Vui lòng đăng ký trước " + timeDKSAString);
			return;
		}
		// end check
		// handle xoa toan bo list cu -> add lai list moi
		List<OverTime> foodOT = OVER_TIME_SERVICE.find(dateSearch, shiftsSelected.getId(), departmentSearch.getCode());
		if (foodOT.size() != 0) {
			// neu da duyet roi thi khong duoc update
			if (foodOT.get(0).isIs_duyet()) {
				Notification.NOTI_ERROR("Trưởng đơn vị đã duyệt không thể chỉnh sửa");
				return;
			}
			boolean deleteNotError = OVER_TIME_SERVICE.delete(foodOT.get(0));
			if (!deleteNotError) {
				Notification.NOTI_ERROR("Lỗi");
				return;
			}
		}

		OverTime entityNew = null;
		List<EmployeeThai> listEmployeeSelected = new ArrayList<>();

		for (EmployeeThai e : employeesThai) {
			if (e.isSelect()) {
				listEmployeeSelected.add(e);
			}
		}
		if (listEmployeeSelected.size() != 0) {
			OverTime overTimeTemp = new OverTime();
			overTimeTemp.setCreatedDate(new Date());
			overTimeTemp.setCreatedUser(member.getCode());
			overTimeTemp.setDepartment_code(listEmployeeSelected.get(0).getDepartmentCode());
			overTimeTemp.setDepartment_name(listEmployeeSelected.get(0).getDepartmentName());
			overTimeTemp.setFood_date(dateSearch);
			overTimeTemp.setShifts(shiftsSelected);
			if (daDuyetSuatAnView) {
				overTimeTemp.setIs_duyet(true);
			}
			entityNew = OVER_TIME_SERVICE.create(overTimeTemp);
			if (entityNew != null) {
				for (EmployeeThai e : listEmployeeSelected) {
					FoodOverTime foodOTTemp = new FoodOverTime();
					foodOTTemp.setEmployee_code(e.getEmployeeCode());
					foodOTTemp.setEmployee_name(e.getEmployeeName());
					foodOTTemp.setOver_time(entityNew);
					if (e.getEmployeeCodeOld() != null) {
						foodOTTemp.setEmployee_code_old(e.getEmployeeCodeOld());
					}
					FoodOverTime checkCreate = FOOD_OVER_TIME_SERVICE.create(foodOTTemp);
					if (checkCreate == null) {
						Notification.NOTI_ERROR("Lỗi");
						return;
					}
				}
			}
			// khong them duoc
			else {
				Notification.NOTI_ERROR("Lỗi");
				return;
			}
		}
		MessageView.INFO("Thành công");
	}

	// ktra co phai ngay hien tai hay khong
	public boolean isCurrentDate(Date dateCheck, String hhmm) throws ParseException {
		Date dateCurrent = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// neu la ngay hien tai
		if (sdf.format(dateCurrent).equals(sdf.format(dateCheck))) {
			return trong.lixco.com.bean.staticentity.DateUtil.compareHHMM(dateCurrent, hhmm);
		}
		// // 3h ngay hom truoc
		// Date dateTomorrow = DateUtil.addDays(dateCurrent, 1);
		// // neu ngay check == ngay mai va 3h ngay hien tai
		// if (sdf.format(dateTomorrow).equals(sdf.format(dateCheck))) {
		// // kiem tra gio hien tai voi gio khoa dang ky mon an
		// return
		// trong.lixco.com.bean.staticentity.DateUtil.compareHHMM(dateCurrent,
		// hhmm);
		// }
		return false;
	}

	@Override
	protected Logger getLogger() {
		return null;
	}

	public Department getDepartmentSearch() {
		return departmentSearch;
	}

	public void setDepartmentSearch(Department departmentSearch) {
		this.departmentSearch = departmentSearch;
	}

	public List<Department> getDepartmentSearchs() {
		return departmentSearchs;
	}

	public void setDepartmentSearchs(List<Department> departmentSearchs) {
		this.departmentSearchs = departmentSearchs;
	}

	public List<EmployeeDTO> getEmployeesByDepartment() {
		return employeesByDepartment;
	}

	public void setEmployeesByDepartment(List<EmployeeDTO> employeesByDepartment) {
		this.employeesByDepartment = employeesByDepartment;
	}

	public Date getDateSearch() {
		return dateSearch;
	}

	public void setDateSearch(Date dateSearch) {
		this.dateSearch = dateSearch;
	}

	public int getShifts() {
		return Shifts;
	}

	public void setShifts(int shifts) {
		Shifts = shifts;
	}

	public Shifts getShiftsSelected() {
		return shiftsSelected;
	}

	public void setShiftsSelected(Shifts shiftsSelected) {
		this.shiftsSelected = shiftsSelected;
	}

	public List<Shifts> getAllShifts() {
		return allShifts;
	}

	public void setAllShifts(List<Shifts> allShifts) {
		this.allShifts = allShifts;
	}

	public boolean isDaDuyetSuatAnView() {
		return daDuyetSuatAnView;
	}

	public void setDaDuyetSuatAnView(boolean daDuyetSuatAnView) {
		this.daDuyetSuatAnView = daDuyetSuatAnView;
	}

	public List<EmployeeThai> getEmployeesThai() {
		return employeesThai;
	}

	public void setEmployeesThai(List<EmployeeThai> employeesThai) {
		this.employeesThai = employeesThai;
	}

	public List<EmployeeThai> getEmployeesThaiFilters() {
		return employeesThaiFilters;
	}

	public void setEmployeesThaiFilters(List<EmployeeThai> employeesThaiFilters) {
		this.employeesThaiFilters = employeesThaiFilters;
	}
}

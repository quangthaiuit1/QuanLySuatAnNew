package trong.lixco.com.bean;

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
import trong.lixco.com.jpa.entity.FoodOverTime;
import trong.lixco.com.jpa.entity.OrderFood;
import trong.lixco.com.jpa.entity.OverTime;
import trong.lixco.com.jpa.entity.Shifts;
import trong.lixco.com.servicepublic.EmployeeDTO;
import trong.lixco.com.servicepublic.EmployeeServicePublic;
import trong.lixco.com.servicepublic.EmployeeServicePublicProxy;
import trong.lixco.com.util.DepartmentUtil;

@Named
@ViewScoped
public class DuyetSuatAnTangCa extends AbstractBean<OrderFood> {
	private static final long serialVersionUID = 1L;
	private Department departmentSearch;
	private List<Department> departmentSearchs;
	private List<EmployeeDTO> employeesByDepartment;
	private Date dateSearch;
	private int Shifts = 1;
	private Shifts shiftsSelected;
	private List<Shifts> allShifts;
	private boolean daDuyet = false;
	private Member member;
	private List<Member> members;
	private boolean daDuyetSuatAnView = false;
	private List<EmployeeThai> employeesCustom;
	private List<EmployeeThai> employeesCustomFilters;

	DepartmentServicePublic departmentServicePublic;
	MemberServicePublic memberServicePublic;
	EmployeeServicePublic EMPLOYEE_SERVICE_PUBLIC;

	@Inject
	private ShiftsService SHIFTS_SERVICE;
	@Inject
	private FoodOverTimeService FOOD_OVER_TIME_SERVICE;
	@Inject
	private OverTimeService OVER_TIME_SERVICE;

	@Override
	protected void initItem() {
		// dateSearch = new Date();
		dateSearch = DateUtil.DATE_WITHOUT_TIME(new Date());
		employeesCustom = new ArrayList<>();
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
			daDuyet = false;
			employeesCustom = new ArrayList<>();
			// handle nhan vien da chon
			List<FoodOverTime> foodOT = FOOD_OVER_TIME_SERVICE.find(dateSearch, shiftsSelected.getId(),
					departmentSearch.getCode());
			// check duyet hay chua
			if (foodOT.size() != 0) {
				if (foodOT.get(0).getOver_time().isIs_duyet()) {
					daDuyetSuatAnView = true;
					daDuyet = true;
				}
			}
			// handle hien toan bo danh sach nhan vien
			List<String> departmentsString = new ArrayList<>();
			departmentsString.add(departmentSearch.getCode());
			String[] departmentsStringArr = departmentsString.toArray(new String[departmentsString.size()]);
			EmployeeDTO[] employeesArray = EMPLOYEE_SERVICE_PUBLIC.findByDep(departmentsStringArr);
			for (int i = 0; i < employeesArray.length; i++) {
				EmployeeThai employeeTemp = new EmployeeThai();
				employeeTemp.setEmployeeCode(employeesArray[i].getCode());
				employeeTemp.setEmployeeName(employeesArray[i].getName());
				employeeTemp.setDepartmentCode(employeesArray[i].getCodeDepart());
				employeeTemp.setDepartmentName(employeesArray[i].getNameDepart());
				employeesCustom.add(employeeTemp);
			}
			if (employeesCustom.size() != 0) {
				for (int i = 0; i < foodOT.size(); i++) {
					for (int j = 0; j < employeesCustom.size(); j++) {
						if (foodOT.get(i).getEmployee_code().equals(employeesCustom.get(j).getEmployeeCode())) {
							employeesCustom.get(j).setSelect(true);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveOrUpdate(){
		
	}
	

	public void saveOrUpdateManager() {
		// handle xoa toan bo list cu -> add lai list moi

		List<OverTime> foodOT = OVER_TIME_SERVICE.find(dateSearch, shiftsSelected.getId(), departmentSearch.getCode());
		if (foodOT.size() != 0) {
			// neu da duyet roi thi khong duoc update
			if (foodOT.get(0).isIs_duyet() && daDuyet) {
				Notification.NOTI_ERROR("Trưởng đơn vị đã duyệt không thể chỉnh sửa");
				return;
			}
			// neu da duyet roi thi khong duoc update
			if (foodOT.get(0).isIs_duyet() && !daDuyet) {
				foodOT.get(0).setIs_duyet(false);
				OVER_TIME_SERVICE.update(foodOT.get(0));
				searchItem();
				MessageView.INFO("Thành công");
				PrimeFaces.current().ajax().update("formDuyetTangCa:dtSuatTangCaTP1");
				return;
			}
			// kiem tra nut duyet co duoc check chua
			if (daDuyet) {
				// tim danh sach nhan vien tang ca tren view
				boolean haveEmpOT = false;
				for (EmployeeThai e : employeesCustom) {
					if (e.isSelect()) {
						haveEmpOT = true;
						break;
					}
				}
				if (!haveEmpOT) {
					Notification.NOTI_ERROR("Vui lòng chọn nhân viên trước khi duyệt");
					return;
				}
				// co nhan vien tang ca
				if (haveEmpOT) {
					foodOT.get(0).setIs_duyet(true);
					OverTime o = OVER_TIME_SERVICE.update(foodOT.get(0));
					if (o != null) {
						List<FoodOverTime> foodOTByOTId = FOOD_OVER_TIME_SERVICE.find(o.getId(), null);
						// bang chi tiet tang ca co nhan vien
						if (foodOTByOTId.size() != 0) {
							for (int i = 0; i < employeesCustom.size(); i++) {
								// boolean isItemNew = false;
								boolean isHaveNV = false;
								int indexNVExisted = 0;
								for (int j = 0; j < foodOTByOTId.size(); j++) {
									// khong duoc check tren view nhung trong db
									// co
									if (foodOTByOTId.get(j).getEmployee_code()
											.equals(employeesCustom.get(i).getEmployeeCode())) {
										isHaveNV = true;
										indexNVExisted = j;
									}
								}
								if (isHaveNV) {
									if (!employeesCustom.get(i).isSelect()) {
										// xoa item do duoi db
										try {
											FOOD_OVER_TIME_SERVICE.delete(foodOTByOTId.get(indexNVExisted));
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
								if (!isHaveNV) {
									if (employeesCustom.get(i).isSelect()) {
										FoodOverTime foodOTTemp = new FoodOverTime();
										foodOTTemp.setEmployee_code(employeesCustom.get(i).getEmployeeCode());
										foodOTTemp.setEmployee_name(employeesCustom.get(i).getEmployeeName());
										foodOTTemp.setOver_time(o);
										try {
											FOOD_OVER_TIME_SERVICE.create(foodOTTemp);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
							PrimeFaces.current().ajax().update("formDuyetTangCa:dtSuatTangCaTP1");
							MessageView.INFO("Thành công");
							return;
						}
						// bang chi tiet tang ca khong co nhan vien -> them moi
						if (foodOTByOTId.size() == 0) {
							List<EmployeeThai> listEmployeeSelected = new ArrayList<>();
							for (int i = 0; i < employeesCustom.size(); i++) {
								if (employeesCustom.get(i).isSelect()) {
									listEmployeeSelected.add(employeesCustom.get(i));
								}
							}
							if (listEmployeeSelected.size() != 0) {
								for (int i = 0; i < listEmployeeSelected.size(); i++) {
									FoodOverTime foodOTTemp = new FoodOverTime();
									foodOTTemp.setEmployee_code(listEmployeeSelected.get(i).getEmployeeCode());
									foodOTTemp.setEmployee_name(listEmployeeSelected.get(i).getEmployeeName());
									foodOTTemp.setOver_time(o);
									try {
										FOOD_OVER_TIME_SERVICE.create(foodOTTemp);
									} catch (Exception e2) {
										e2.printStackTrace();
									}
								}
							}
							PrimeFaces.current().ajax().update("formDuyetTangCa:dtSuatTangCaTP1");
							MessageView.INFO("Thành công");
							return;
						}
					}
				}
			}
			// boolean deleteNotError = OVER_TIME_SERVICE.delete(foodOT.get(0));
			// if (!deleteNotError) {
			// Notification.NOTI_ERROR("Lỗi");
			// return;
			// }
		}
		// chua co thong tin tang ca tu phong hanh chinh
		if (foodOT.size() == 0) {
			// chua duyet
			OverTime entityNew = null;
			List<EmployeeThai> listEmployeeSelected = new ArrayList<>();

			for (EmployeeThai e : employeesCustom) {
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
				if (daDuyet) {
					overTimeTemp.setIs_duyet(true);
				}
				entityNew = OVER_TIME_SERVICE.create(overTimeTemp);
				if (entityNew != null) {
					for (EmployeeThai e : listEmployeeSelected) {
						FoodOverTime foodOTTemp = new FoodOverTime();
						foodOTTemp.setEmployee_code(e.getEmployeeCode());
						foodOTTemp.setEmployee_name(e.getEmployeeName());
						foodOTTemp.setOver_time(entityNew);
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

	public boolean isDaDuyet() {
		return daDuyet;
	}

	public void setDaDuyet(boolean daDuyet) {
		this.daDuyet = daDuyet;
	}

	public boolean isDaDuyetSuatAnView() {
		return daDuyetSuatAnView;
	}

	public void setDaDuyetSuatAnView(boolean daDuyetSuatAnView) {
		this.daDuyetSuatAnView = daDuyetSuatAnView;
	}

	public List<EmployeeThai> getEmployeesCustom() {
		return employeesCustom;
	}

	public void setEmployeesCustom(List<EmployeeThai> employeesCustom) {
		this.employeesCustom = employeesCustom;
	}

	public List<EmployeeThai> getEmployeesCustomFilters() {
		return employeesCustomFilters;
	}

	public void setEmployeesCustomFilters(List<EmployeeThai> employeesCustomFilters) {
		this.employeesCustomFilters = employeesCustomFilters;
	}
}

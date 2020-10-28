package trong.lixco.com.bean.entities;

public class EmployeeThai {
	private String employeeCode;
	private String employeeName;
	private String departmentCode;
	private String departmentName;
	private String employeeCodeOld;
	private boolean isSelect = false;

	public String getEmployeeCodeOld() {
		return employeeCodeOld;
	}

	public void setEmployeeCodeOld(String employeeCodeOld) {
		this.employeeCodeOld = employeeCodeOld;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
}

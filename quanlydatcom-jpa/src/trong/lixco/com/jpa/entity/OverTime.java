package trong.lixco.com.jpa.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "over_time")
public class OverTime extends AbstractEntity {
	private String department_code;
	private String department_name;
	private boolean is_duyet = false;
	@OneToOne
	private Shifts shifts;
	private Date food_date;

	public String getDepartment_code() {
		return department_code;
	}

	public void setDepartment_code(String department_code) {
		this.department_code = department_code;
	}

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}

	public boolean isIs_duyet() {
		return is_duyet;
	}

	public void setIs_duyet(boolean is_duyet) {
		this.is_duyet = is_duyet;
	}

	public Shifts getShifts() {
		return shifts;
	}

	public void setShifts(Shifts shifts) {
		this.shifts = shifts;
	}

	public Date getFood_date() {
		return food_date;
	}

	public void setFood_date(Date food_date) {
		this.food_date = food_date;
	}
}

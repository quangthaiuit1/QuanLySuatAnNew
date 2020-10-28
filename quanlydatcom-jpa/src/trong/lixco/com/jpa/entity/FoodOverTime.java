package trong.lixco.com.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "food_over_time")
public class FoodOverTime extends AbstractEntity {
	private String employee_code;
	private String employee_name;
	private String employee_code_old;

	@OneToOne
	private OverTime over_time;

	public String getEmployee_code_old() {
		return employee_code_old;
	}

	public void setEmployee_code_old(String employee_code_old) {
		this.employee_code_old = employee_code_old;
	}

	public String getEmployee_code() {
		return employee_code;
	}

	public void setEmployee_code(String employee_code) {
		this.employee_code = employee_code;
	}

	public String getEmployee_name() {
		return employee_name;
	}

	public void setEmployee_name(String employee_name) {
		this.employee_name = employee_name;
	}

	public OverTime getOver_time() {
		return over_time;
	}

	public void setOver_time(OverTime over_time) {
		this.over_time = over_time;
	}
}

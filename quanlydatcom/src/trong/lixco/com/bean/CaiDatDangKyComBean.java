package trong.lixco.com.bean;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.bean.staticentity.MessageView;
import trong.lixco.com.ejb.service.TimeBoundService;
import trong.lixco.com.jpa.entity.TimeBound;

@ViewScoped
@Named
public class CaiDatDangKyComBean extends AbstractBean<TimeBound> {

	private static final long serialVersionUID = 1L;
	@Inject
	private TimeBoundService TIME_BOUND_SERVICE;
	private int hour = 0;
	private int minute = 0;
	private TimeBound timebound;
	private int isAllowRegister = 0;

	@Override
	protected void initItem() {
		timebound = TIME_BOUND_SERVICE.find("DKSA");
		if (timebound != null && timebound.getId() != 0) {
			hour = timebound.getHour();
			minute = Integer.parseInt(timebound.getMinutes());
		}
		TimeBound t = TIME_BOUND_SERVICE.find("CURRENTDATE");
		if (t != null && t.getId() != 0) {
			isAllowRegister = t.isIs_allow_register_current() ? 1 : 0;
		}
	}

	public void saveOrUpdateTimeBound() {
		try {
			timebound.setHour(hour);
			timebound.setMinutes(Integer.toString(minute));
			TIME_BOUND_SERVICE.update(timebound);
			MessageView.INFO("Thành công");
		} catch (Exception e) {
			e.printStackTrace();
			MessageView.ERROR("Lỗi");
		}
	}

	public void saveOrUpdateIsAllow() {
		try {
			TimeBound t = TIME_BOUND_SERVICE.find("CURRENTDATE");
			boolean isAllow = false;
			if (isAllowRegister == 0) {
				isAllow = false;
			}
			if (isAllowRegister == 1) {
				isAllow = true;
			}
			t.setIs_allow_register_current(isAllow);
			TIME_BOUND_SERVICE.update(t);
			MessageView.INFO("Thành công");
		} catch (Exception e) {
			e.printStackTrace();
			MessageView.ERROR("Lỗi");
		}
	}

	@Override
	protected Logger getLogger() {
		return null;
	}

	public int getIsAllowRegister() {
		return isAllowRegister;
	}

	public void setIsAllowRegister(int isAllowRegister) {
		this.isAllowRegister = isAllowRegister;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

}

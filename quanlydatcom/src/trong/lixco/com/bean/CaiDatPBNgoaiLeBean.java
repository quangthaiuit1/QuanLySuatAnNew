package trong.lixco.com.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.account.servicepublics.Department;
import trong.lixco.com.account.servicepublics.DepartmentServicePublic;
import trong.lixco.com.account.servicepublics.DepartmentServicePublicProxy;
import trong.lixco.com.bean.staticentity.MessageView;
import trong.lixco.com.ejb.service.DepartmentExceptionService;
//import trong.lixco.com.ejb.service.DepartmentExceptionService;
import trong.lixco.com.jpa.entity.DepartException;
import trong.lixco.com.util.DepartmentUtil;

@Named
@ViewScoped
public class CaiDatPBNgoaiLeBean extends AbstractBean<DepartException> {
	private static final long serialVersionUID = 1L;

	private List<DepartException> departExs;
	private List<Department> departmentSearchs;

	@Inject
	DepartmentExceptionService DEPARTMENT_EXCEPTION_SERVICE;
	DepartmentServicePublic DEPARTMENT_SERVICE_PUBLIC;

	@Override
	protected void initItem() {
		departExs = DEPARTMENT_EXCEPTION_SERVICE.findAll();
		if (departExs == null || departExs.isEmpty()) {
			departExs = new ArrayList<>();
		}
		departmentSearchs = new ArrayList<Department>();
		DEPARTMENT_SERVICE_PUBLIC = new DepartmentServicePublicProxy();
		try {
			Department[] deps = DEPARTMENT_SERVICE_PUBLIC.findAll();
			for (int i = 0; i < deps.length; i++) {
				if (deps[i].getLevelDep() != null)
					if (deps[i].getLevelDep().getLevel() > 1)
						departmentSearchs.add(deps[i]);
			}
			if (departmentSearchs.size() != 0) {
				departmentSearchs = DepartmentUtil.sort(departmentSearchs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < departExs.size(); i++) {
			for (int j = 0; j < departmentSearchs.size(); j++) {
				if (departExs.get(i).getCode().equals(departmentSearchs.get(j).getCode())) {
					departmentSearchs.get(j).setSelect(true);
					break;
				}
			}
		}
	}

	public void saveOrUpdate() {
		// ds phong ban duoc chon
		List<Department> depsTemp = new ArrayList<>();
		for (Department d : departmentSearchs) {
			if (d.isSelect()) {
				depsTemp.add(d);
			}
		}

		// database chua co row nao
		if (departExs.isEmpty()) {
			for (Department d : depsTemp) {
				DepartException y = new DepartException();
				y.setCode(d.getCode());
				y.setName(d.getName());
				try {
					DEPARTMENT_EXCEPTION_SERVICE.create(y);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			MessageView.INFO("Thành công");
			return;
		}
		// list moi empty
		if (depsTemp.isEmpty()) {
			for (DepartException d : departExs) {
				try {
					DEPARTMENT_EXCEPTION_SERVICE.delete(d);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			MessageView.INFO("Thành công");
			return;
		}

		// handle khi luu xuong db
		for (int i = 0; i < departExs.size(); i++) {
			boolean isExist = false;
			for (int j = 0; j < depsTemp.size(); j++) {
				if (departExs.get(i).getCode().equals(depsTemp.get(j).getCode())) {
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				// tien hanh xoa
				try {
					DEPARTMENT_EXCEPTION_SERVICE.delete(departExs.get(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (int i = 0; i < depsTemp.size(); i++) {
			boolean isExist = false;
			for (int j = 0; j < departExs.size(); j++) {
				if (departExs.get(j).getCode().equals(depsTemp.get(i).getCode())) {
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				// tien hanh them vo
				DepartException d = new DepartException();
				d.setCode(depsTemp.get(i).getCode());
				d.setName(depsTemp.get(i).getName());
				try {
					DEPARTMENT_EXCEPTION_SERVICE.create(d);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		departExs = DEPARTMENT_EXCEPTION_SERVICE.findAll();
		// update lish depart
		for (int i = 0; i < departExs.size(); i++) {
			for (int j = 0; j < departmentSearchs.size(); j++) {
				if (departExs.get(i).getCode().equals(departmentSearchs.get(j).getCode())) {
					departmentSearchs.get(j).setSelect(true);
					break;
				}
			}
		}

		MessageView.INFO("Thành công");
	}

	@Override
	protected Logger getLogger() {
		return null;
	}

	public List<DepartException> getDepartExs() {
		return departExs;
	}

	public void setDepartExs(List<DepartException> departExs) {
		this.departExs = departExs;
	}

	public List<Department> getDepartmentSearchs() {
		return departmentSearchs;
	}

	public void setDepartmentSearchs(List<Department> departmentSearchs) {
		this.departmentSearchs = departmentSearchs;
	}
}

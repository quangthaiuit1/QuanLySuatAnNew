package trong.lixco.com.ejb.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import trong.lixco.com.jpa.entity.FoodOverTime;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class FoodOverTimeService extends AbstractService<FoodOverTime> {
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;

	@Override
	protected Class<FoodOverTime> getEntityClass() {
		return FoodOverTime.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}

	public List<FoodOverTime> find(java.util.Date dateSearch, long shiftsId, String departmentCode) {
		// primary
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FoodOverTime> cq = cb.createQuery(FoodOverTime.class);
		Root<FoodOverTime> root = cq.from(FoodOverTime.class);
		List<Predicate> queries = new ArrayList<>();
		if (dateSearch != null) {
			Predicate dateQuery = cb.equal(root.get("over_time").get("food_date"), dateSearch);
			queries.add(dateQuery);
		}
		if (shiftsId != 0) {
			Predicate shiftsQuery = cb.equal(root.get("over_time").get("shifts").get("id"), shiftsId);
			queries.add(shiftsQuery);
		}
		if (departmentCode != null) {
			Predicate shiftsQuery = cb.equal(root.get("over_time").get("department_code"), departmentCode);
			queries.add(shiftsQuery);
		}

		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<FoodOverTime> query = em.createQuery(cq);
		List<FoodOverTime> results = query.getResultList();
		if (!results.isEmpty()) {
			return results;
		} else {
			return new ArrayList<FoodOverTime>();
		}
	}

	public List<FoodOverTime> findByDaDuyet(java.util.Date dateSearch, long shiftsId, String departmentCode) {
		// primary
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FoodOverTime> cq = cb.createQuery(FoodOverTime.class);
		Root<FoodOverTime> root = cq.from(FoodOverTime.class);
		List<Predicate> queries = new ArrayList<>();
		if (dateSearch != null) {
			Predicate dateQuery = cb.equal(root.get("over_time").get("food_date"), dateSearch);
			queries.add(dateQuery);
		}
		if (shiftsId != 0) {
			Predicate shiftsQuery = cb.equal(root.get("over_time").get("shifts").get("id"), shiftsId);
			queries.add(shiftsQuery);
		}
		if (departmentCode != null) {
			Predicate shiftsQuery = cb.equal(root.get("over_time").get("department_code"), departmentCode);
			queries.add(shiftsQuery);
		}
		Predicate duyetQuery = cb.equal(root.get("over_time").get("is_duyet"), true);
		queries.add(duyetQuery);
		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<FoodOverTime> query = em.createQuery(cq);
		List<FoodOverTime> results = query.getResultList();
		if (!results.isEmpty()) {
			return results;
		} else {
			return new ArrayList<FoodOverTime>();
		}
	}

	public List<FoodOverTime> find(long overtimeId, String employeeCode) {
		// primary
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FoodOverTime> cq = cb.createQuery(FoodOverTime.class);
		Root<FoodOverTime> root = cq.from(FoodOverTime.class);
		List<Predicate> queries = new ArrayList<>();
		if (overtimeId != 0) {
			Predicate shiftsQuery = cb.equal(root.get("over_time").get("id"), overtimeId);
			queries.add(shiftsQuery);
		}
		if (employeeCode != null) {
			Predicate shiftsQuery = cb.equal(root.get("employee_code"), employeeCode);
			queries.add(shiftsQuery);
		}
		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<FoodOverTime> query = em.createQuery(cq);
		List<FoodOverTime> results = query.getResultList();
		if (!results.isEmpty()) {
			return results;
		} else {
			return new ArrayList<FoodOverTime>();
		}
	}
}

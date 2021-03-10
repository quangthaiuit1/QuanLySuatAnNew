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

import trong.lixco.com.jpa.entity.Link;
import trong.lixco.com.jpa.entity.TimeBound;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class LinkService extends AbstractService<Link>{
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;

	@Override
	protected Class<Link> getEntityClass() {
		return Link.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}

	public Link find(String name) {
		// primary
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Link> cq = cb.createQuery(Link.class);
		Root<Link> root = cq.from(Link.class);
		List<Predicate> queries = new ArrayList<>();
		if (name != null) {
			Predicate shiftsQuery = cb.equal(root.get("link_name"), name);
			queries.add(shiftsQuery);
		}

		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<Link> query = em.createQuery(cq);
		List<Link> results = query.getResultList();
		if (!results.isEmpty()) {
			return results.get(0);
		} else {
			return new Link();
		}
	}
}

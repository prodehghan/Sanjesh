package controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.ValidationException;

import core.NotLoggedInException;
import core.Utils;
import dao.ArbiterDao;
import dao.DesignerDao;
import dao.SanjeshAgentDao;
import dao.UniversityAgentDao;
import dao.UserDao;

import model.Arbiter;
import model.Designer;
import model.Person;
import model.SanjeshAgent;
import model.UniversityAgent;
import model.User;

@ManagedBean
@ViewScoped
public class UserProfileController {

	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	private User currentUser;
	private Person relatedPerson;

	@Inject
	private SanjeshAgentDao sanjeshAgentDao;
	@Inject
	private UniversityAgentDao universityAgentDao;
	@Inject
	private DesignerDao designerDao;
	@Inject
	private ArbiterDao arbiterDao;
	@Inject
	private UserDao userDao;

	private boolean editUserName = false;
	private boolean editPassword = false;

	private String oldPassword, newPassword, confirmNewPassword;

	@PostConstruct
	public void init() {
		if (loginBean == null || loginBean.getCurrentUser() == null) {
			throw new NotLoggedInException();
		}
		currentUser = loginBean.reloadCurrentUser();
		relatedPerson = loginBean.getRelatedPerson();
	}
	
	private void refreshRelatedPerson(){
		if (relatedPerson instanceof UniversityAgent)
			relatedPerson = universityAgentDao.refresh((UniversityAgent) relatedPerson);
		else if (relatedPerson instanceof SanjeshAgent)
			relatedPerson = sanjeshAgentDao.refresh((SanjeshAgent) relatedPerson);
		else if (relatedPerson instanceof Designer)
		    relatedPerson = designerDao.refresh((Designer)relatedPerson);
		else if (relatedPerson instanceof Arbiter)
		    relatedPerson = arbiterDao.refresh((Arbiter)relatedPerson);
	}

//	public LoginBean getLoginBean() {
//		return loginBean;
//	}

	public void setLoginBean(LoginBean lb) {
		this.loginBean = lb;
	}

	public Person getRelatedPerson() {
		return relatedPerson;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public boolean isEditUserName() {
		return editUserName;
	}

	public boolean isEditPassword() {
		return editPassword;
	}

	public void setEditUserName() {
		editPassword = false;
		editUserName = true;
	}

	public void setEditPassword() {
		editUserName = false;
		editPassword = true;
	}

	public void cancelEdit() {
		editUserName = false;
		editPassword = false;
		
		refreshRelatedPerson();
		currentUser = loginBean.reloadCurrentUser();
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}

	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}

	public void saveUserName() {

		try {
			
			userDao.save(currentUser);

			if (relatedPerson != null) {
				if (relatedPerson instanceof UniversityAgent) {
					UniversityAgent ua = (UniversityAgent) relatedPerson;
					ua.setUser(currentUser);
					universityAgentDao.save(ua);
				} else if (relatedPerson instanceof SanjeshAgent) {
					SanjeshAgent sa = (SanjeshAgent) relatedPerson;
					sa.setUser(currentUser);
					sanjeshAgentDao.save(sa);
				} else if (relatedPerson instanceof Designer) {
				    Designer d = (Designer) relatedPerson;
				    d.setUser(currentUser);
				    designerDao.save(d);
				} else if (relatedPerson instanceof Arbiter) {
				    Arbiter a = (Arbiter) relatedPerson;
				    a.setUser(currentUser);
				    arbiterDao.save(a);
				} else {
				    userDao.save(currentUser);
				}
			}

			currentUser = loginBean.reloadCurrentUser();

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "تغییرات ثبت شدند.", null));

			editUserName = false;
			editPassword = false;
			
		} catch (Throwable e) {
			if( Utils.handleBeanException(e))
				return;
			else
				throw e;
		}
	}

	public void savePassword() {

		try {

			currentUser = loginBean.reloadCurrentUser();

			if (!oldPassword.equals(currentUser.getPassword())) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"رمز قبلی وارد شده صحیح نمی باشد.", null));
				return;
			}

			if (!confirmNewPassword.equals(newPassword)) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"رمز جدید مطابق تکرار آن نمیباشد.",
								"لطفاً با دقت بیشتری رمز و تکرار آن را وارد نمایید."));
				return;
			}

			currentUser.setPassword(newPassword);
			userDao.save(currentUser);

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "رمز جدید ثبت شد.", null));

			editUserName = false;
			editPassword = false;

		} catch (EJBException e) {
			if (e.getCause() instanceof ValidationException) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().getMessage(),
								null));
				return;
			}
			throw e;
		}
	}
}

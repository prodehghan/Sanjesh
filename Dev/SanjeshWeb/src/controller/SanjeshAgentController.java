﻿package controller;

import dao.SanjeshAgentDao;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import model.SanjeshAgent;

/**
 *
 * @author Abbas
 */
@ManagedBean
@ViewScoped
public class SanjeshAgentController extends EntityControllerBase<SanjeshAgent> {
    
    @Inject
    private SanjeshAgentDao dao;
        
	private String passwordCoinfirmation;
	
    @PostConstruct
    public void init() {
        super.init(dao);
    }
    
    @Override
    public String getEntityName() {
        return "نماینده سازمان سنجش";
    }
    
	public String getPasswordCoinfirmation() {
		return passwordCoinfirmation;
	}

	public void setPasswordCoinfirmation(String passwordCoinfirmation) {
		this.passwordCoinfirmation = passwordCoinfirmation;
	}

	@Override
	public void save() {
		SanjeshAgent sa = this.getToEdit();
		if (!this.getPasswordCoinfirmation().equals(
				sa.getUser().getPassword())) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"رمز عبور وارد شده مطابق تکرار آن نمیباشد.", "لطفاً با دقت بیشتری رمز و تکرار آن را وارد نمایید."));
			return;
		}
		else{
			super.save();
			this.setPasswordCoinfirmation(null);
		}
	}
	
	@Override
	public void edit(SanjeshAgent sa){
		super.edit(sa);
		this.setPasswordCoinfirmation(sa.getUser().getPassword());
	}
	
}

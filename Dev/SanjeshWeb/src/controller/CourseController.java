﻿package controller;

import dao.CourseDao;
import dao.EducationFieldDao;
import javax.faces.bean.ViewScoped;
import model.Course;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import model.EducationField;

/**
 *
 * @author Muhammad
 */
@ManagedBean
@ViewScoped
public class CourseController extends EntityControllerBase<Course> {

    @Inject
    private CourseDao dao;
    @Inject
    private EducationFieldDao fieldDao;
    
    private DataModel<Course> list2;
    
    private boolean showingTopicList = false;

    public CourseController() {
    }

    @PostConstruct
    public void init() {
        super.init(dao);
    }
    
    @Override
    public String getEntityName() {
        return "درس";
    }
    
    @Override
    public void saveAndNew() {
        EducationField field = getToEdit().getField();
        super.saveAndNew();
        getToEdit().setField(field);
    }
    
    public List<EducationField> getEducationFields(){
        return fieldDao.findAll();
    }
    
    public int getSelectedFieldId(){
        if( getToEdit().getField() != null)
            return getToEdit().getField().getId();
        return 0;
    }
    
    public void setSelectedFieldId(int id){
        this.getToEdit().setField(fieldDao.findById(id));
    }
    
    public boolean isShowingTopicList() {
        return showingTopicList;
    }
    
    public void showTopicList() {
        dao.fillTopics(toEdit);
        showingTopicList = true;
    }
    
    @Override
    public void showList() {
        super.showList();
        showingTopicList = false;
    }
    
    public DataModel<Course> getList2() {
        return list2;
    }
    
    @Override
    public void loadList() {
        super.loadList();
        list2 = new ListDataModel<Course>(super.getList());
    }

}

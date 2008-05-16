package de.mpg.escidoc.pubman.util;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;

import org.apache.myfaces.trinidad.component.UIXIterator;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.easySubmission.EasySubmission;
import de.mpg.escidoc.pubman.easySubmission.EasySubmissionSessionBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.pubman.util.statistics.PubItemSimpleStatistics;
import de.mpg.escidoc.pubman.util.statistics.SimpleStatistics;
import de.mpg.escidoc.services.common.valueobjects.FileVO;

public class PubFileVOPresentation extends FacesBean {

	public static final String FILE_TYPE_FILE = "FILE";
	public static final String FILE_TYPE_LOCATOR = "LOCATOR";
	private int index;
	private FileVO file;
	private HtmlCommandButton removeButton = new HtmlCommandButton();
	private boolean isLocator = false;
	private String fileType;


    /**
     * The possible content types of a file.
     * @updated 21-Nov-2007 12:05:47
     */
    public enum ContentCategory
    {
        ANY_FULLTEXT, PRE_PRINT, POST_PRINT, PUBLISHER_VERSION, ABSTRACT, TABLE_OF_CONTENTS, SUPPLEMENTARY_MATERIAL, CORRESPONDENCE, COPYRIGHT_TRANSFER_AGREEMENT
    }

	public PubFileVOPresentation()
	{
		this.file = new FileVO();
	}
	
	public PubFileVOPresentation(int fileIndex, boolean isLocator)
	{
		this.file = new FileVO();
		this.index = fileIndex; 
		this.isLocator = isLocator;
	}
	
	public PubFileVOPresentation(int fileIndex, FileVO file)
	{
		this.index = fileIndex; 
		this.file = file;
		this.removeButton.setTitle("btnRemove_" + fileIndex);
	}

	public PubFileVOPresentation(int fileIndex, FileVO file, boolean isLocator)
	{
		this.index = fileIndex; 
		this.file = file;
		this.removeButton.setTitle("btnRemove_" + fileIndex);
		this.isLocator = isLocator;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public FileVO getFile() {
		return file;
	}

	public void setFile(FileVO file) {
		this.file = file;
	}

	public HtmlCommandButton getRemoveButton() {
		return removeButton;
	}

	public void setRemoveButton(HtmlCommandButton removeButton) {
		this.removeButton = removeButton;
	}
	
	public boolean getIsLocator() {
		return isLocator;
	}

	public void setLocator(boolean isLocator) {
		this.isLocator = isLocator;
	}

	public String getFileType() {
		return fileType;
	}
	
	public String getContentCategory()
    {
    	String contentCategory = "";
    	InternationalizedImpl internationalized = new InternationalizedImpl();
    	if(this.file.getContentCategory() != null)
    	{
    		contentCategory = internationalized
    		    .getLabel(
    		            this
    		                .i18nHelper
    		                .convertEnumToString(
    		                        PubFileVOPresentation.ContentCategory.valueOf(
    		                                CommonUtils.convertToEnumString(
    		                                        this.file.getContentCategory()))));
    	}
    	return contentCategory;
    }
	
	public String getVisibility()
    {
    	String visibility = "";
    	InternationalizedImpl internationalized = new InternationalizedImpl();
    	if(this.file.getVisibility() != null)
    	{
    		visibility = internationalized.getLabel(this.i18nHelper.convertEnumToString(this.file.getVisibility()));
    	}
    	return visibility;
    }

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	
	public String removeFile ()
	{
		EditItemSessionBean editItemSessionBean = (EditItemSessionBean)getSessionBean(EditItemSessionBean.class);
 		
		editItemSessionBean.getFiles().remove(this.index);
		
		// ensure that at least one file component is visible
		if(editItemSessionBean.getFiles().size() == 0)
		{
			editItemSessionBean.getFiles().add(0, new PubFileVOPresentation(0, false));
		}
		
		editItemSessionBean.reorganizeFileIndexes();
		return "loadEditItem";		
	}
	
	public String removeLocatorEditItem ()
	{
		EditItemSessionBean editItemSessionBean = (EditItemSessionBean)getSessionBean(EditItemSessionBean.class);
 		
		editItemSessionBean.getLocators().remove(this.index);
		
		// ensure that at least one locator component is visible
		if(editItemSessionBean.getLocators().size() == 0)
		{
			editItemSessionBean.getLocators().add(0, new PubFileVOPresentation(0, true));
		}
		
		editItemSessionBean.reorganizeLocatorIndexes();
		return "loadEditItem";		
	}
	
	public String removeFileEasySubmission ()
	{
		EasySubmission easySubmission = (EasySubmission)getSessionBean(EasySubmission.class); 
		EasySubmissionSessionBean easySubmissionSessionBean = this.getEasySubmissionSessionBean();
 		
 		easySubmissionSessionBean.getFiles().remove(this.index);
 		easySubmission.reorganizeFileIndexes();
 		easySubmission.setFileIterator(new UIXIterator());
 		easySubmission.init();
		return "loadNewEasySubmission";		
	}
	
	public String removeLocatorEasySubmission ()
	{
		EasySubmission easySubmission = (EasySubmission)getSessionBean(EasySubmission.class); 
		EasySubmissionSessionBean easySubmissionSessionBean = this.getEasySubmissionSessionBean();
 		
 		easySubmissionSessionBean.getLocators().remove(this.index);
 		easySubmission.reorganizeLocatorIndexes();
 		easySubmission.setLocatorIterator(new UIXIterator());
 		easySubmission.init();
		return "loadNewEasySubmission";		
	}
	
	/**
     * Returns the EasySubmissionSessionBean.
     *
     * @return a reference to the scoped data bean (EasySubmissionSessionBean)
     */
    protected EasySubmissionSessionBean getEasySubmissionSessionBean()
    {
    	return (EasySubmissionSessionBean) getSessionBean(EasySubmissionSessionBean.class);
    }
    
    /**
     * Returns the ApplicationBean.
     * 
     * @return a reference to the scoped data bean (ApplicationBean)
     */
    protected ApplicationBean getApplicationBean()
    {
        return (ApplicationBean) FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), ApplicationBean.BEAN_NAME);
        
    }


	
	public String getNumberOfFileDownloadsPerFileAllUsers() throws Exception
    {
        
        String fileID = file.getReference().getObjectId();
        PubItemSimpleStatistics stat = new SimpleStatistics();
        String result = stat.getSimpleStatisticValue(PubItemSimpleStatistics.REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ALL_USERS, fileID);
        return result;
    }
    
    public String getNumberOfFileDownloadsPerFileAnonymousUsers() throws Exception
    {
        String fileID = file.getReference().getObjectId();
        PubItemSimpleStatistics stat = new SimpleStatistics();
        String result = stat.getSimpleStatisticValue(PubItemSimpleStatistics.REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ANONYMOUS, fileID);
        return result;
    }
}
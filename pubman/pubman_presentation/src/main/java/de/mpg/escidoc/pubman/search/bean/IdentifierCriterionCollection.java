package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.services.pubman.valueobjects.IdentifierCriterionVO;

/**
 * Bean to handle the IdentifierCriterionCollection on a single jsp.
 * A IdentifierCriterionCollection is represented by a List<IdentifierCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class IdentifierCriterionCollection
{
	public static final String BEAN_NAME = "IdentifierCriterionCollection";
	
	private List<IdentifierCriterionVO> parentVO;
	private IdentifierCriterionManager identifierCriterionManager;
	
	/**
	 * CTOR to create a new ArrayList<IdentifierCriterionVO> 
	 * starting with one empty new IdentifierCriterionVO
	 */
	public IdentifierCriterionCollection()
	{
		// ensure the parentVO is never null;
		List<IdentifierCriterionVO> ctorList = new ArrayList<IdentifierCriterionVO>();
		ctorList.add(new IdentifierCriterionVO());
		setParentVO(ctorList);
	}

	/**
	 * CTOR to refine or fill a predefined ArrayList<IdentifierCriterionVO>
	 * @param parentVO
	 */
	public IdentifierCriterionCollection(List<IdentifierCriterionVO> parentVO)
	{
		setParentVO(parentVO);
	}

	public List<IdentifierCriterionVO> getParentVO()
	{
		return parentVO;
	}

	public void setParentVO(List<IdentifierCriterionVO> parentVO)
	{
		this.parentVO = parentVO;
		// ensure proper initialization of our DataModelManager
		identifierCriterionManager = new IdentifierCriterionManager(parentVO);
	}
	
	/**
	 * Specialized DataModelManager to deal with objects of type IdentifierCriterionBean
	 * @author Mario Wagner
	 */
	public class IdentifierCriterionManager extends DataModelManager<IdentifierCriterionBean>
	{
		List<IdentifierCriterionVO> parentVO;
		
		public IdentifierCriterionManager(List<IdentifierCriterionVO> parentVO)
		{
			setParentVO(parentVO);
		}
		
		public IdentifierCriterionBean createNewObject()
		{
			IdentifierCriterionVO newVO = new IdentifierCriterionVO();
			// create a new wrapper pojo
			IdentifierCriterionBean identifierCriterionBean = new IdentifierCriterionBean(newVO);
			// we do not have direct access to the original list
			// so we have to add the new VO on our own
			parentVO.add(newVO);
			return identifierCriterionBean;
		}
		
		@Override
		protected void removeObjectAtIndex(int i)
		{
			// due to wrapped data handling
			super.removeObjectAtIndex(i);
			parentVO.remove(i);
		}

		public List<IdentifierCriterionBean> getDataListFromVO()
		{
			if (parentVO == null) return null;
			// we have to wrap all VO's in a nice IdentifierCriterionBean
			List<IdentifierCriterionBean> beanList = new ArrayList<IdentifierCriterionBean>();
			for (IdentifierCriterionVO identifierCriterionVO : parentVO)
			{
				beanList.add(new IdentifierCriterionBean(identifierCriterionVO));
			}
			return beanList;
		}

		public void setParentVO(List<IdentifierCriterionVO> parentVO)
		{
			this.parentVO = parentVO;
			// we have to wrap all VO's into a nice IdentifierCriterionBean
			List<IdentifierCriterionBean> beanList = new ArrayList<IdentifierCriterionBean>();
			for (IdentifierCriterionVO identifierCriterionVO : parentVO)
			{
				beanList.add(new IdentifierCriterionBean(identifierCriterionVO));
			}
			setObjectList(beanList);
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}


	public IdentifierCriterionManager getIdentifierCriterionManager()
	{
		return identifierCriterionManager;
	}

	public void setIdentifierCriterionManager(IdentifierCriterionManager identifierCriterionManager)
	{
		this.identifierCriterionManager = identifierCriterionManager;
	}

    public void clearAllForms()
    {        
    	for (IdentifierCriterionBean gcb : identifierCriterionManager.getObjectList())
    	{
    		gcb.clearCriterion();
    	}
    }

    public List<IdentifierCriterionVO> getFilledCriterionVO()
	{
    	List<IdentifierCriterionVO> returnList = new ArrayList<IdentifierCriterionVO>();
    	for (IdentifierCriterionVO vo : parentVO)
    	{
    		if ((vo.getSearchString() != null && vo.getSearchString().length() > 0))
    		{
    			returnList.add(vo);
    		}
    	}
		return returnList;
	}

}

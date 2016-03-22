/**
 * 
 */
package de.mpg.escidoc.services.tools.scripts.person_grants.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.GrantVO;

/**
 * Data object representing a person
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Person {
    private String id;
    private String name;
	private String loginname;
	

    private List<String> grantList;
	
	
	/**
	 * instantiates a new person object
	 */
	public Person() {
		super();
		this.grantList = new ArrayList<String>();
	}
	
	/**
	 * instantiates a new person object
	 * @param givenname of the person
	 * @param surname of the person
	 * @param mailAdress of the person
	 */
	public Person(String name, String loginname, List<String> grantList) {
		super();
		this.name = name;
		this.loginname = loginname;
		this.grantList = grantList;
	}
	
	
	/**
	 * instantiates a new person object
	 * @param givenname
	 * @param surname
	 * @param mailAdress
	 * @param password
	 */
	public Person(String name, String mailAdress,
			String password) {
		super();
		this.name = name;
	}
	
	/**
     * @return id of the person
     */
    public String getId() {
        return id;
    }

    /**
     * @param id of the person
     */
    public void setId(String id) {
        this.id = id;
    }
	
	/**
	 * @return name of the person
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name of the person
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return loginname of the person
	 */
	public String getLoginname()
    {
        return loginname;
    }

    /**
     * @param loginname of the person
     */
    public void setLoginname(String loginname)
    {
        this.loginname = loginname;
    }

    /**
     * @return grantList of the person
     */
    public List<String> getGrantList()
    {
        return grantList;
    }

    /**
     * @param grantList of the person
     */
    public void setGrantList(List<String> grantList)
    {
        this.grantList = grantList;
    }
    
    public void addGrant(String grant)
    {
        this.grantList.add(grant);
    }

	public boolean isEmpty() {
		boolean empty = true;
		try {
			for (Field field : this.getClass().getDeclaredFields()) {
				//set fields to public
				field.setAccessible(true); 
					if (field.get(this) != null) {
						empty = false;
						break;
					}
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Error getting field  ["
					+ Person.class.getEnclosingMethod() + "]");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println("Error getting field ["
					+ Person.class.getEnclosingMethod() + "]");
			e.printStackTrace();
		}
		return empty;
	}
}

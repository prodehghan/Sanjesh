﻿package dao;


import model.SanjeshAgent;

/**
 *
 * @author Abbas
 */

public interface SanjeshAgentDao extends DaoBase<SanjeshAgent>{
	public SanjeshAgent findByUser(int userId);
}

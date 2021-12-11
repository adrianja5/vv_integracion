package com.practica.integracion;
import com.practica.integracion.*;
import com.practica.integracion.DAO.AuthDAO;
import com.practica.integracion.DAO.GenericDAO;
import com.practica.integracion.DAO.User;
import com.practica.integracion.manager.SystemManager;
import com.practica.integracion.manager.SystemManagerException;

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;


import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestValidUser {

	//Creación de los Mocks
	@Mock
	AuthDAO authDAO;
	@Mock
	GenericDAO genericDAO;

	List<Object> roles=new LinkedList<Object>();

	User user=new User("21", "Manuel", "Bollain", "calle", roles);
	private User use;


	@Test
	@DisplayName("Test startRemoteSystem")
	public void testStartRemoteSystem() throws OperationNotSupportedException, SystemManagerException {

		when(authDAO.getAuthData("21")).thenReturn(user);
		when(genericDAO.getSomeData(user,"where id=21")).thenReturn(roles);

		SystemManager systemManager = new SystemManager(authDAO,genericDAO);
		Collection<Object> salida= systemManager.startRemoteSystem("21","21");

		assertEquals(salida,new ArrayList());

		InOrder inOrder = inOrder(authDAO, genericDAO);
		inOrder.verify(authDAO).getAuthData("21");
		inOrder.verify(genericDAO).getSomeData(user,"where id=21");

	}


	/*	StopRemoteSystem	*/
	@Test
	@DisplayName("Test stopRemoteSystem")
	public void testStopRemoteSystem() throws OperationNotSupportedException, SystemManagerException {

		when(authDAO.getAuthData("21")).thenReturn(user);
		when(genericDAO.getSomeData(user,"where id=21")).thenReturn(roles);

		SystemManager systemManager = new SystemManager(authDAO,genericDAO);
		Collection<Object> salida= systemManager.startRemoteSystem("21","21");

		assertEquals(salida,new ArrayList());

		InOrder inOrder = inOrder(authDAO, genericDAO);
		inOrder.verify(authDAO).getAuthData("21");
		inOrder.verify(genericDAO).getSomeData(user,"where id=21");

	}



	/*	AddRemoteSystem	*/
	@Test
	@DisplayName("Test addRemoteSystem")
	public void testAddRemoteSystem() throws OperationNotSupportedException, SystemManagerException {

		Object remote = null;
		when(authDAO.getAuthData("21")).thenReturn(user);
		when(genericDAO.updateSomeData(user,remote)).thenReturn(true);

		SystemManager systemManager = new SystemManager(authDAO,genericDAO);
		systemManager.addRemoteSystem(user.getId(),remote);

		InOrder inOrder = inOrder(authDAO, genericDAO);
		inOrder.verify(authDAO).getAuthData("21");
		inOrder.verify(genericDAO).updateSomeData(user,remote);


	}

	@Test
	@DisplayName("Test Fallo al añadir un remote")
	public void testFailAddRemoteSystem() throws OperationNotSupportedException, SystemManagerException {

		Object remote = null;
		when(authDAO.getAuthData("21")).thenReturn(user);
		when(genericDAO.updateSomeData(user,remote)).thenReturn(false);

		SystemManager systemManager = new SystemManager(authDAO,genericDAO);
		Exception thrown = assertThrows(
				Exception.class,
				() -> systemManager.addRemoteSystem(user.getId(),remote),
				"Fail to add new remote system"
		);
		assertTrue(thrown.getMessage().contains("cannot add remote"));

		InOrder inOrder = inOrder(authDAO, genericDAO);
		inOrder.verify(authDAO).getAuthData("21");
		inOrder.verify(genericDAO).updateSomeData(user,remote);
	}




	/*	DeleteRemoteSystem	*/
	@Test
	@DisplayName("Test deleteRemoteSystem")
	public void testDeleteRemoteSystem() throws OperationNotSupportedException, SystemManagerException {

		when(genericDAO.deleteSomeData(any(),any())).thenReturn(true);

		SystemManager systemManager = new SystemManager(authDAO,genericDAO);
		systemManager.deleteRemoteSystem("1","1");

		InOrder inOrder = inOrder(authDAO, genericDAO);
		inOrder.verify(genericDAO).deleteSomeData(any(),any());

	}

	@Test
	@DisplayName("Test fallo al borrar un servicio")
	public void testFailDeleteRemoteSystem() throws OperationNotSupportedException, SystemManagerException {

		when(genericDAO.deleteSomeData(any(),any())).thenReturn(false);
		SystemManager systemManager = new SystemManager(authDAO,genericDAO);

		SystemManagerException thrown = assertThrows(
				SystemManagerException.class,
				() -> systemManager.deleteRemoteSystem("1","1"),
				"Fail to delete remote system"
		);
		assertTrue(thrown.getMessage().contains("cannot delete remote: does remote exists?"));

		InOrder inOrder = inOrder(authDAO, genericDAO);
		inOrder.verify(genericDAO).deleteSomeData(any(),any());

	}




}

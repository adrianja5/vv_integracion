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

	@Mock	//Estos no tienen nada implementado
	AuthDAO authDAO;

	@Mock	//Estos no tienen nada implementado
	GenericDAO genericDAO;

	@InjectMocks //Los metemos en la clase creo, o algo así
	SystemManager systemManager;

	List<Object> roles=new LinkedList<Object>();


	User user=new User("21", "Manuel", "Bollain", "calle", roles);//Esta  no hace falta mock, ya que si la podemos instanciar
	private User use;


	@Test
	@DisplayName("Test startRemoteSystem")
	public void testStartRemoteSystem() throws OperationNotSupportedException, SystemManagerException {

		//authDAO.getAuthData("21");// Esta función ahora mismo no está implementada por lo que tendríamos que "mockearla" .
		//Cuando se llame a esta funcion desde dentro de startRemoteSistem devolvera lo que ahora pongamos en el when

		when(authDAO.getAuthData("21")).thenReturn(user);//Es decir, cuando se llame a .getAuth desde StartRemote y se envie el id=21 nos devolverá a Manuel
		when(genericDAO.getSomeData(user,"where id=21")).thenReturn(roles);//cuadno se llame a getSomeData

		Collection<Object> salida= systemManager.startRemoteSystem("21","21");

		assertEquals(salida,new ArrayList());//Como roles esta vacia

		//Esto para comprobar que primero se llama a Auth y luego a getSomeData
		InOrder inOrder = inOrder(authDAO, genericDAO);
		//following will make sure that firstMock was called before secondMock
		inOrder.verify(authDAO).getAuthData("21");
		inOrder.verify(genericDAO).getSomeData(user,"where id=21");

	}


	/*	StopRemoteSystem	*/
	@Test
	@DisplayName("Test stopRemoteSystem")
	public void testStopRemoteSystem() throws OperationNotSupportedException, SystemManagerException {

		when(authDAO.getAuthData("21")).thenReturn(user);
		when(genericDAO.getSomeData(user,"where id=21")).thenReturn(roles);

		Collection<Object> salida= systemManager.startRemoteSystem("21","21");

		assertEquals(salida,new ArrayList());//Como roles esta vacia lo comprobamos con una nueva lista

		//Esto para comprobar que primero se llama a Auth y luego a getSomeData
		InOrder inOrder = inOrder(authDAO, genericDAO);
		//following will make sure that firstMock was called before secondMock
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
		systemManager.deleteRemoteSystem("1","1");

		InOrder inOrder = inOrder(authDAO, genericDAO);
		//inOrder.verify(authDAO).getAuthData("1");
		inOrder.verify(genericDAO).deleteSomeData(any(),any());

	}

	@Test
	@DisplayName("Test fallo al borrar un servicio")
	public void testFailDeleteRemoteSystem() throws OperationNotSupportedException, SystemManagerException {

		when(genericDAO.deleteSomeData(any(),any())).thenReturn(false);

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

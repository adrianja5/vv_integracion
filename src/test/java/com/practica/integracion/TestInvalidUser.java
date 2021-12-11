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
public class TestInvalidUser {


    @Mock	//Estos no tienen nada implementado
            AuthDAO authDAO;

    @Mock	//Estos no tienen nada implementado
            GenericDAO genericDAO;



    List<Object> roles=new LinkedList<Object>();

    //Habría que hacer un nuevo usuario que no "funciona" aunque sea igual pero para que quede mejor


    User user=new User("21", "Manuel", "Bollain", "calle", roles);//Esta  no hace falta mock, ya que si la podemos instanciar
    private User use;

    @Test
    @DisplayName("Test startRemoteSystem Invalid User Detected")
    public void testInvalidStartRemoteSystem() throws OperationNotSupportedException, SystemManagerException {

        OperationNotSupportedException excep =new OperationNotSupportedException("No se puede hacer la operación");
        when(authDAO.getAuthData("21")).thenReturn(user);
        when(genericDAO.getSomeData(user,"where id=21")).thenThrow(excep);
        SystemManager systemManager = new SystemManager(authDAO,genericDAO);

        SystemManagerException thrown = assertThrows(
                SystemManagerException.class,
                () -> systemManager.startRemoteSystem("21","21")
        );
        assertTrue(thrown.getMessage().contains("No se puede hacer la operación"));


        InOrder inOrder = inOrder(authDAO, genericDAO);
        inOrder.verify(authDAO).getAuthData("21");
        inOrder.verify(genericDAO).getSomeData(user,"where id=21");

    }


    @Test
    @DisplayName("Test startRemoteSystem Invalid User Detected")
    public void testInvalidStopRemoteSystem() throws OperationNotSupportedException, SystemManagerException {

        OperationNotSupportedException excep =new OperationNotSupportedException("No se puede hacer la operación: Invalid User");
        when(authDAO.getAuthData("21")).thenReturn(user);
        when(genericDAO.getSomeData(user,"where id=21")).thenThrow(excep);
        SystemManager systemManager = new SystemManager(authDAO,genericDAO);

        SystemManagerException thrown = assertThrows(
                SystemManagerException.class,
                () -> systemManager.stopRemoteSystem("21","21")
        );
        assertTrue(thrown.getMessage().contains("No se puede hacer la operación: Invalid User"));//Comprobamos que la excepción sea esa


        InOrder inOrder = inOrder(authDAO, genericDAO);
        inOrder.verify(authDAO).getAuthData("21");
        inOrder.verify(genericDAO).getSomeData(user,"where id=21");

    }


    @Test
    @DisplayName("Test addRemoteSystem Invalid User Detected")
    public void testInvalidAddRemoteSystem() throws OperationNotSupportedException, SystemManagerException {

        Object remote = null;
        OperationNotSupportedException excep =new OperationNotSupportedException("No se puede hacer la operación de añadir: Invalid User");
        when(authDAO.getAuthData("21")).thenReturn(user);
        when(genericDAO.updateSomeData(any(),any())).thenThrow(excep);

        SystemManager systemManager = new SystemManager(authDAO,genericDAO);
        SystemManagerException thrown = assertThrows(
                SystemManagerException.class,
                () -> systemManager.addRemoteSystem("21",remote)
        );
        assertTrue(thrown.getMessage().contains("No se puede hacer la operación de añadir: Invalid User"));


        InOrder inOrder = inOrder(authDAO, genericDAO);
        inOrder.verify(authDAO).getAuthData("21");
        inOrder.verify(genericDAO).updateSomeData(user,remote);


    }

    @Test
    @DisplayName("Test fallo al borrar un servicio Invalid User Detected")
    public void testInvalidDeleteRemoteSystem() throws OperationNotSupportedException, SystemManagerException {


        OperationNotSupportedException excep =new OperationNotSupportedException("No se puede hacer la operación de borrar: Invalid User");
        when(genericDAO.deleteSomeData(any(),any())).thenThrow(excep);
        SystemManager systemManager = new SystemManager(authDAO,genericDAO);

        SystemManagerException thrown = assertThrows(
                SystemManagerException.class,
                () -> systemManager.deleteRemoteSystem("1","1")
        );
        assertTrue(thrown.getMessage().contains("No se puede hacer la operación de borrar: Invalid User"));

        InOrder inOrder = inOrder(authDAO, genericDAO);
        inOrder.verify(genericDAO).deleteSomeData(any(),any());

    }




}

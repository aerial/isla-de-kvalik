package com.example.dbmanager.client;

import com.example.dbmanager.domain.AppContext;
import com.example.dbmanager.domain.Person;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DBManagerLogin implements EntryPoint {
    private final DBManagerServiceAsync dbmanagerService = GWT.create(DBManagerService.class);


    public void onModuleLoad() {
        final Window loginWindow = new Window();
        loginWindow.setPlain(true);
        loginWindow.setSize(300, 200);
        loginWindow.setLayout(new FitLayout());

        loginWindow.setHeading("Login");
        final TextField loginTF = new TextField();
        final TextField passwordTF = new TextField();
        final FormPanel formPanel = new FormPanel();
        loginTF.setEmptyText("Login");
        passwordTF.setEmptyText("Password");

        formPanel.setHeading("Edit Person");
        formPanel.setWidth(350);
        loginTF.setAllowBlank(false);
        loginTF.setFieldLabel("Login");
        passwordTF.setAllowBlank(false);
        passwordTF.setFieldLabel("Password");

        formPanel.add(loginTF, new FormData("100%"));
        formPanel.add(passwordTF, new FormData("100%"));

        loginWindow.add(formPanel);
        Button loginButton = new Button("Login");
//        loginButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
//            @Override
//            public void handleEvent(BaseEvent be) {
//
//                dbmanagerService.login(loginTF.getValue().toString(),passwordTF.getValue().toString(), new AsyncCallback<Person>() {
//                    @Override
//                    public void onFailure(Throwable caught) {
//                        loginWindow.setHeading("Fail");
//                    }
//
//                    @Override
//                    public void onSuccess(Person result) {
//                        loginWindow.setHeading("Success " + result.getLogin());
//
//                        switch (result.getRole()) {
//                            case 0: {
//
//                                break; }
//                            case 1: {
//                                ProgrammerEntryPoint programmerPage = new ProgrammerEntryPoint();
//                                programmerPage.init(dbmanagerService);
//                                programmerPage.onModuleLoad();
//                                break; }
//                            case 2: {
//                                ManagerEntryPoint managerPage = new ManagerEntryPoint();
//                                managerPage.init(dbmanagerService);
//                                managerPage.onModuleLoad();
//                                break;}
//                        }
//                        loginWindow.hide();
//
//
//                    }
//                });
//
//
//            }
//        });
        loginWindow.addButton(loginButton);
        loginWindow.show();
        ManagerWorkspace homepage = new ManagerWorkspace();
        homepage.init(dbmanagerService, new AppContext());
        homepage.onModuleLoad();

    }

    private void reloadPersons(){

    }

    private void createPerson() {
        final EditPersonWindow editPersonWindow = new EditPersonWindow();
        Button saveButton = new Button("Save");
        saveButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                final Person newPerson = new Person();
                newPerson.setFirstName(editPersonWindow.getFirstName());
                newPerson.setLastName(editPersonWindow.getLastName());
                newPerson.setAge(editPersonWindow.getAge());
                dbmanagerService.savePerson(newPerson, new AsyncCallback<Long>() {
                    @Override
                    public void onFailure(Throwable caught) {}
                    @Override
                    public void onSuccess(Long result) {
                        editPersonWindow.close();
                        reloadPersons();
                    }
                });
            }
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                editPersonWindow.close();
            }
        });
        editPersonWindow.addButton(saveButton);
        editPersonWindow.addButton(cancelButton);
        editPersonWindow.show();
    }
}
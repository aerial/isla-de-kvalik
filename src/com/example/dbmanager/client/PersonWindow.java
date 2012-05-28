package com.example.dbmanager.client;

import com.example.dbmanager.domain.Person;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.cell.client.TextButtonCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.List;

public class PersonWindow  extends  Window{
    private final DBManagerServiceAsync dbmanagerService = GWT.create(DBManagerService.class);
    private PersonWindow personWindow;

    public void reloadPersons(){
        personWindow =  new PersonWindow();

        RpcProxy<List<Person>> proxy = new RpcProxy<List<Person>>() {
            @Override
            protected void load(Object loadConfig, AsyncCallback<List<Person>> callback) {
                dbmanagerService.getPeople(callback);
            }
        };
        BeanModelReader reader = new BeanModelReader();

        ListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
        final ListStore<BeanModel> store = new ListStore<BeanModel>(loader);
        loader.load();

        ColumnModel cm = new ColumnModel(getColumnConfig());

        ContentPanel panel = new ContentPanel();
        panel.setHeading("testTitle");
        panel.setFrame(true);
        panel.setSize("500", "300");
        panel.setLayout(new FitLayout());

        Grid<BeanModel> grid = new Grid<BeanModel>(store, cm);
        grid.setAutoExpandColumn("firstName");
        grid.addListener(Events.CellDoubleClick, new Listener<GridEvent<ModelData>>() {
            @Override
            public void handleEvent(GridEvent<ModelData> be) {
                final EditPersonWindow editPersonWindow = new EditPersonWindow(be.getModel());
                //editPersonWindow.setHeading(be.getModel().get("id").toString());
                Long id =(Long) be.getModel().get("id");
                final List<Person> pList = new ArrayList<Person>();
                dbmanagerService.findPersonById(id, new AsyncCallback<Person>() {
                    @Override public void onFailure(Throwable caught) {}
                    @Override
                    public void onSuccess(Person result) {
                        pList.add(result);
                    }
                });
                Button saveButton = new Button("Save");
                saveButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
                    @Override
                    public void handleEvent(BaseEvent be) {
                        if (pList.size()>0) {
                            final Person updatePerson = pList.get(0);
                            updatePerson.setFirstName(editPersonWindow.getFirstName());
                            updatePerson.setLastName(editPersonWindow.getLastName());
                            updatePerson.setAge(editPersonWindow.getAge());
                            dbmanagerService.updatePerson(updatePerson, new AsyncCallback() {
                                @Override public void onFailure(Throwable caught) { }
                                @Override
                                public void onSuccess(Object result) {
                                    editPersonWindow.close();
                                    personWindow.close();
                                    reloadPersons();
                                }
                            });
                        }
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
        });
        panel.add(grid);

        personWindow.setPlain(true);
        personWindow.setSize(600, 400);
        personWindow.setHeading("Person");
        personWindow.setLayout(new FitLayout());
        personWindow.add(panel);
        personWindow.show();
    }

    private List<ColumnConfig> getColumnConfig () {
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        ColumnConfig column = new ColumnConfig("id", "ID", 50);
        columns.add(column);

        column = new ColumnConfig("age", "Age", 50);
        NumberField field = new NumberField();
        field.setPropertyEditorType(Integer.class);
        column.setEditor(new CellEditor(field));
        columns.add(column);

        column = new ColumnConfig("firstName", "First name", 200);
        TextField<String> text = new TextField<String>();
        // text.setAllowBlank(false);
        column.setEditor(new CellEditor(text));
        columns.add(column);

        column = new ColumnConfig("lastName", "Last name", 200);
        text = new TextField<String>();
        column.setEditor(new CellEditor(text));
        columns.add(column);

        column = new ColumnConfig("delete", "Delete", 70);
        column.setRenderer(new GridCellRenderer() {
            @Override
            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore listStore, Grid grid) {
                final Long id = model.get("id");
                Button button = new Button("Delete");
                button.addListener(Events.OnClick, new Listener<BaseEvent>() {
                    @Override
                    public void handleEvent(BaseEvent be) {
                        dbmanagerService.removePerson(id, new AsyncCallback<Integer>() {
                            @Override public void onFailure(Throwable caught) {}
                            @Override
                            public void onSuccess(Integer result) {
                                personWindow.close();
                                personWindow.reloadPersons();
                            }
                        });
                    }
                });
                return button;
            }
        });
        columns.add(column);
        return columns;
    }
}
package project.workshop.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.workshop.Main;
import project.workshop.db.DbIntegrityException;
import project.workshop.gui.listeners.DataChangeListener;
import project.workshop.gui.util.Alerts;
import project.workshop.gui.util.Utils;
import project.workshop.model.entities.Employee;
import project.workshop.model.services.DepartmentService;
import project.workshop.model.services.EmployeeService;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EmployeeListController implements Initializable, DataChangeListener {

    private EmployeeService service;

    @FXML
    private TableView<Employee> tableViewEmployee;

    @FXML
    private TableColumn<Employee, Integer> tableColumnId;

    @FXML
    private TableColumn<Employee, String> tableColumnName;

    @FXML
    private TableColumn<Employee, String> tableColumnEmail;

    @FXML
    private TableColumn<Employee, Date> tableColumnBirthDate;

    @FXML
    private TableColumn<Employee, Double> tableColumnBaseSalary;

    @FXML
    private TableColumn<Employee, Employee> tableColumnEDIT;

    @FXML
    private TableColumn<Employee, Employee> tableColumnDELETE;

    @FXML
    private Button btNew;

    private ObservableList<Employee> obsList;

    @FXML
    public void onBtNewAction(ActionEvent event) {
        Stage parentStage = Utils.currentStage(event);
        Employee employee = new Employee();
        createDialogForm(employee, "/project/workshop/gui/EmployeeForm.fxml", parentStage);
    }

    public void setEmployeeService(EmployeeService service) {
        this.service = service;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewEmployee.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView(){
        if (service == null){
            throw new IllegalStateException("Service is null");
        }
        List<Employee> employees = service.findAll();
        obsList = FXCollections.observableArrayList(employees);
        tableViewEmployee.setItems(obsList);
        initEditButtons();
        initDeleteButtons();
    }

    private void createDialogForm(Employee employee, String absoluteName, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            EmployeeFormController controller = loader.getController();
            controller.setEmployee(employee);
            controller.setServices(new EmployeeService(), new DepartmentService());
            controller.loadAssociatedObjects();
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Insira os dados do Funcionário");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e){
            e.printStackTrace();
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }

    //REFERENCE: https://stackoverflow.com/questions/32282230/fxml-javafx-8-tableview-make-a-delete-button-in-each-row-and-delete-the-row-a
    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Employee, Employee>() {
            private final Button button = new Button("Editar");

            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                if (employee == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(employee, "/project/workshop/gui/EmployeeForm.fxml",Utils.currentStage(event)));
            }
        });
    }

    //REFERENCE: https://stackoverflow.com/questions/32282230/fxml-javafx-8-tableview-make-a-delete-button-in-each-row-and-delete-the-row-a
    private void initDeleteButtons() {
        tableColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnDELETE.setCellFactory(param -> new TableCell<Employee, Employee>() {
            private final Button button = new Button("Deletar");
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                if (employee == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> deleteEntity(employee));
            }
        });
    }

    private void deleteEntity(Employee employee) {
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza que quer deletar?");

        if (result.get() == ButtonType.OK) {
            if (service == null){
                throw new IllegalStateException("Service is null");
            }
            try {
                service.delete(employee);
                updateTableView();
            } catch (DbIntegrityException e) {
                Alerts.showAlert("Error deleting object", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}

package com.topi.themealdb.bean;

import com.topi.themealdb.modelo.Meal;
import com.topi.themealdb.servico.MealServico;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class MealBean implements Serializable {

    private MealServico mealServico = new MealServico();

    private List<Meal> listaMeals;

    @PostConstruct
    public void inicializaListagem() throws IOException {
        listaMeals = mealServico.retornaTodasMeals();
    }

    public List<Meal> getListaMeals() {
        return listaMeals;
    }

    public void setListaMeals(List<Meal> listaMeals) {
        this.listaMeals = listaMeals;
    }
}

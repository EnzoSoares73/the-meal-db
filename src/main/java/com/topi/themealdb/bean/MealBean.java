package com.topi.themealdb.bean;

import com.topi.themealdb.modelo.Meal;
import com.topi.themealdb.servico.MealServico;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class MealBean implements Serializable {

    private final int NUM_CHARS = 200;

    private MealServico mealServico = new MealServico();

    private List<Meal> listaMeals;
    private Meal meal;

    @PostConstruct
    public void inicializaListagem() throws IOException {
        listaMeals = mealServico.retornaTodasMeals();
    }

    public void visualizarMeal(int id) throws IOException {
        setMeal(mealServico.obterMealPorId(id));
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(context.getRequestContextPath() + "/webpages/meal.xhtml");
    }

    public String reduzirTexto(String string) {
        if (string.length() > NUM_CHARS) {
            string = string.substring(0, NUM_CHARS) + "...";
        }
        return string;
    }

    public List<Meal> getListaMeals() {
        return listaMeals;
    }

    public void setListaMeals(List<Meal> listaMeals) {
        this.listaMeals = listaMeals;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }
}

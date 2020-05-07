package com.outerspace.luis_viruena_baking2.model;

public final class RecipeModelFactory {
    private static RecipeModelImpl instance;

    private RecipeModelFactory() { }

    public final static class Builder {
        private ModelBehavior behavior = ModelBehavior.NETWORK_REQUEST;
        public Builder setBehavior(ModelBehavior behavior) {
            this.behavior = behavior;
            return this;
        }

        public IRecipeModel build() {
            RecipeModelFactory factory = new RecipeModelFactory();
            return factory.getInstance(behavior);
        }
    }

    private IRecipeModel getInstance(ModelBehavior behavior) {
        if(instance == null) {
            instance = new RecipeModelImpl();
        }
        return  instance;
    }
}

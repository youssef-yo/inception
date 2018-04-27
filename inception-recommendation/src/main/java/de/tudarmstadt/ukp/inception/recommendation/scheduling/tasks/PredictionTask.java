/*
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.inception.recommendation.scheduling.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationLayer;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.security.model.User;
import de.tudarmstadt.ukp.inception.recommendation.api.ClassificationTool;
import de.tudarmstadt.ukp.inception.recommendation.api.Classifier;
import de.tudarmstadt.ukp.inception.recommendation.api.RecommendationService;
import de.tudarmstadt.ukp.inception.recommendation.api.model.AnnotationObject;
import de.tudarmstadt.ukp.inception.recommendation.api.model.Predictions;
import de.tudarmstadt.ukp.inception.recommendation.api.model.Recommender;
import de.tudarmstadt.ukp.inception.recommendation.api.model.TokenObject;
import de.tudarmstadt.ukp.inception.recommendation.imls.util.CasUtil;

/**
 * This consumer predicts new annotations for a given annotation layer, if a classification tool for
 * this layer was selected previously.
 */
public class PredictionTask
    extends Task
{
    private Logger log = LoggerFactory.getLogger(getClass());
    
    private @Autowired AnnotationSchemaService annoService;
    private @Autowired RecommendationService recommendationService;
    private @Autowired DocumentService documentService;
    
    public PredictionTask(User aUser, Project aProject)
    {
        super(aProject, aUser);
    }

    @Override
    public void run()
    {
        User user = getUser();

        Predictions model = new Predictions(getProject(), getUser()); 

        for (AnnotationLayer layer : annoService.listAnnotationLayer(getProject())) {
            if (!layer.isEnabled()) {
                continue;
            }
            
            List<Recommender> recommenders = recommendationService.getActiveRecommenders(user,
                    layer);

            if (recommenders.isEmpty()) {
                log.debug("[{}][{}]: No active recommenders, skipping prediction.",
                        user.getUsername(), layer.getUiName());
                continue;
            }
            
            for (Recommender recommender : recommenders) {
                long startTime = System.currentTimeMillis();
                
                ClassificationTool<?> ct = recommendationService.getTool(recommender,
                        recommendationService.getMaxSuggestions(user));
                Classifier<?> classifier = ct.getClassifier();

                classifier.setUser(getUser());
                classifier.setProject(getProject());
                classifier.setModel(recommendationService.getTrainedModel(user, recommender));
    
                List<List<TokenObject>> tokens = new ArrayList<>();
                    
                List<AnnotationDocument> docs = documentService
                        .listAnnotationDocuments(layer.getProject(), user);
                docs.forEach(doc -> {
                    JCas jcas;
                    try {
                        jcas = documentService.readAnnotationCas(doc);
                        tokens.addAll(CasUtil.loadTokenObjects(jcas, 0, 
                                jcas.getDocumentText().length()));
                    } catch (IOException e) {
                        log.error("Cannot read annotation CAS.", e);
                    }
                });
      
                if (tokens.isEmpty()) {
                    log.info("[{}][{}]: No training data.", user.getUsername(),
                            recommender.getName());
                    return;
                }
                
                log.info("[{}][{}]: Predicting labels...", user.getUsername(),
                        recommender.getName());
                List<AnnotationObject> predictions = classifier.predict(tokens, layer);
                predictions.forEach(token -> token.setRecommenderId(ct.getId()));
                
                model.putPredictions(layer.getId(), predictions);
                
                log.info("[{}][{}]: Prediction complete ({} ms)", user.getUsername(),
                        recommender.getName(), (System.currentTimeMillis() - startTime));
            }
        }
        
        recommendationService.putIncomingPredictions(getUser(), getProject(), model);
    }
}

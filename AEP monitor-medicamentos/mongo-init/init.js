// Script de inicialização do MongoDB — roda automaticamente na primeira vez
// que o container sobe (ver docker-compose.yml).
// Cria a coleção única "medicamentos" já com validação de schema,
// conforme exigido na 1ª entrega da AEP (coleção única, documentos homogêneos).

db = db.getSiblingDB("monitor_medicamentos");

db.createCollection("medicamentos", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["nomePaciente", "nomeMedicamento", "dosagem", "horario", "tomado"],
            properties: {
                nomePaciente: {
                    bsonType: "string",
                    description: "Nome do paciente é obrigatório"
                },
                nomeMedicamento: {
                    bsonType: "string",
                    description: "Nome do medicamento é obrigatório"
                },
                dosagem: {
                    bsonType: "string",
                    description: "Dosagem é obrigatória"
                },
                horario: {
                    bsonType: "string",
                    description: "Horário é obrigatório"
                },
                tomado: {
                    bsonType: "bool",
                    description: "Indica se a dose já foi tomada"
                }
            }
        }
    },
    validationLevel: "moderate"
});

db.medicamentos.createIndex({ nomePaciente: 1 });

print("Coleção 'medicamentos' criada com validação de schema e índice em nomePaciente.");

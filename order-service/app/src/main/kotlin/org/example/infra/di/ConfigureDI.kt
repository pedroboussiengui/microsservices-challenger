package org.example.infra.di

import io.ktor.server.application.*
import io.ktor.util.*
import org.example.application.usecase.CreateOrder
import org.example.application.usecase.RetrieveOrderById
import org.example.application.usecase.UpdateOrder
import org.example.infra.database.DatabaseConnection
import org.example.infra.database.PostgresOrderRepository
import org.example.infra.messaging.RabbitMessageConsumer
import org.example.infra.messaging.RabbitMessagePublisher
import org.example.infra.messaging.RabbitMqConfig
import org.example.infra.messaging.RabbitMqConnection

val rabbitMqPublisherKey = AttributeKey<RabbitMessagePublisher>("rabbitMqPublisher")
val rabbitMqConsumerKey = AttributeKey<RabbitMessageConsumer>("rabbitMqConsumer")
private val postgresOrderRepositoryKey = AttributeKey<PostgresOrderRepository>("orderRepository")
private val createOrderUseCaseKey = AttributeKey<CreateOrder>("createOrderUseCase")
private val retrieveOrderByIdKey = AttributeKey<RetrieveOrderById>("retrieveOrderById")
val updateOrderKey = AttributeKey<UpdateOrder>("updateOrder")

fun Application.configureDI() {

    DatabaseConnection.init(
        "jdbc:postgresql://localhost:5432/microservices",
        "postgres",
        "postgres"
    )

    val rabbitMqConnection = RabbitMqConnection.init(
        "localhost",
        5672,
        "guest",
        "guest"
    )
    RabbitMqConfig(rabbitMqConnection).setup()

    val publisher = RabbitMessagePublisher(rabbitMqConnection)
    attributes.put(rabbitMqPublisherKey, publisher)

    val consumer = RabbitMessageConsumer(rabbitMqConnection)
    attributes.put(rabbitMqConsumerKey, consumer)

    val orderRepository = PostgresOrderRepository()
    attributes.put(postgresOrderRepositoryKey, orderRepository)

    val createOrderUseCase = CreateOrder(orderRepository, publisher)
    attributes.put(createOrderUseCaseKey, createOrderUseCase)

    val retrieveOrderById = RetrieveOrderById(orderRepository)
    attributes.put(retrieveOrderByIdKey, retrieveOrderById)

    val updateOrder = UpdateOrder(orderRepository)
    attributes.put(updateOrderKey, updateOrder)
}

val Application.rabbitMqProducer: RabbitMessagePublisher
    get() = attributes[rabbitMqPublisherKey]

val Application.rabbitMqConsumer: RabbitMessageConsumer
    get() = attributes[rabbitMqConsumerKey]

val Application.postgresOrderRepository: PostgresOrderRepository
    get() = attributes[postgresOrderRepositoryKey]

val Application.createOrderUseCase: CreateOrder
    get() = attributes[createOrderUseCaseKey]

val Application.retrieveOrderByIdUseCase: RetrieveOrderById
    get() = attributes[retrieveOrderByIdKey]

val Application.updateOrderUseCase: UpdateOrder
    get() = attributes[updateOrderKey]



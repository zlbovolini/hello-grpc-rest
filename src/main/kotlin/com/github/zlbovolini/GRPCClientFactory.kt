package com.github.zlbovolini

import com.github.zlbovolini.hello.HelloGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton

@Factory
class GRPCClientFactory {

    @Singleton
    fun helloClientStub(@GrpcChannel("hello") channel: ManagedChannel) : HelloGrpcServiceGrpc.HelloGrpcServiceBlockingStub {
        return HelloGrpcServiceGrpc.newBlockingStub(channel)
    }
}
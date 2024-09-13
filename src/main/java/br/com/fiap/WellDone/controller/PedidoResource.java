package br.com.fiap.WellDone.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.WellDone.model.Pedido;
import br.com.fiap.WellDone.repository.PedidosRep;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(value = "/index_pedido")
public class PedidoResource {

    @Autowired
    private PedidosRep pedidoRepository;

    @Operation(description = "Este serviço retorna todas as pedidos da base de dados", summary = "Retorna todas as pedidos", tags = "Consulta")
    @GetMapping(value = "/todos")
    public List<Pedido> ConsultaTodos() {
        List<Pedido> lista = pedidoRepository.findAll();
        for (Pedido i : lista) {
            i.add(linkTo(methodOn(PedidoResource.class).findById(i.getPedido_id()))
                    .withRel("Quer consultar o pedido por ID? Acesse este link: "));
        }
        return lista;
    }

    @Operation(description = "Este serviço retorna pedidos por id", summary = "Retorna pedido por id", tags = "Consulta")
    @GetMapping(value = "/{pedido_id}")
    public Pedido findById(@PathVariable Long pedido_id) {
        Optional<Pedido> pedido = pedidoRepository.findById(pedido_id);

        pedido.get().add(linkTo(methodOn(PedidoResource.class).ConsultaTodos())
                .withRel("Deseja consultar todos os pedidos? Acesse este link: "));
        pedido.get().add(linkTo(methodOn(PedidoResource.class).inserirPedido(null))
                .withRel("Deseja inserir um pedido? Acesse este link: "));

        return pedido.get();
    }

    @Operation(description = "Este serviço possibilita a inserção de pedidos no banco de dados", summary = "Inserção de pedidos", tags = "Inserção")
    @ResponseStatus(CREATED)
    @PostMapping()
    public Pedido inserirPedido(@RequestBody Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Operation(description = "Este serviço possibilita a remoção de pedidos do banco de dados", summary = "Remoção de pedidos", tags = "Remoção")
    @DeleteMapping(value = "/remove_pedido/{pedido_id}")
    public Pedido apagarPedido(@PathVariable Long pedido_id) {
        Pedido p1 = pedidoRepository.findById(pedido_id).get();
        pedidoRepository.deleteById(pedido_id);
        return p1;
    }

    @Operation(description = "Este serviço possibilita a atualização de pedidos no banco de dados", summary = "Atualização de pedidos", tags = "Atualização")
    @PutMapping(value = "/atualiza_pedido/{pedido_id}")
    public ResponseEntity<Pedido> atualizarPedido(@PathVariable Long pedido_id, @RequestBody Pedido pedidoAtualizado) {
        return pedidoRepository.findById(pedido_id).map(pedido -> {

            pedido.setPedido_status(pedidoAtualizado.getPedido_status());
            pedido.setPedido_data(pedidoAtualizado.getPedido_data());
            pedido.setPedido_cliente_id(pedidoAtualizado.getPedido_cliente_id());
            pedido.setPedido_vlr_total(pedidoAtualizado.getPedido_vlr_total());

            pedido = pedidoRepository.save(pedido);
            return ResponseEntity.ok(pedido);
        }).orElse(ResponseEntity.notFound().build());
    }
}

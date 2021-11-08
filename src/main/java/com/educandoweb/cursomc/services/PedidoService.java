package com.educandoweb.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.educandoweb.cursomc.domain.ItemPedido;
import com.educandoweb.cursomc.domain.PagamentoComBoleto;
import com.educandoweb.cursomc.domain.Pedido;
import com.educandoweb.cursomc.domain.enums.EstadoPagamento;
import com.educandoweb.cursomc.repositories.ItemPedidoRepository;
import com.educandoweb.cursomc.repositories.PagamentoRepository;
import com.educandoweb.cursomc.repositories.PedidoRepository;
import com.educandoweb.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repo;

	@Autowired
	private BoletoService boletoService;

	@Autowired
	private PagamentoRepository pagamentoRepo;

	@Autowired
	private ItemPedidoRepository itemPedidoRepo;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ClienteService clienteService;

	public Pedido findById(Integer id) {
		Optional<Pedido> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id " + id + ", Tipo " + Pedido.class.getName()));
	}

	@Transactional()
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(clienteService.findById(obj.getCliente().getId()));
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		obj = repo.save(obj);
		pagamentoRepo.save(obj.getPagamento());
		for (ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(produtoService.findById(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		itemPedidoRepo.saveAll(obj.getItens());
		System.out.println(obj);
		return obj;
	}
}
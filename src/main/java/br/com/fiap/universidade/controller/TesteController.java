package br.com.fiap.universidade.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.fiap.universidade.model.Discente;
import br.com.fiap.universidade.model.OpcoesNivel;
import br.com.fiap.universidade.model.OpcoesPaises;
import br.com.fiap.universidade.model.OpcoesStatus;
import br.com.fiap.universidade.model.Pessoa;
import br.com.fiap.universidade.repository.DiscenteRepository;
import br.com.fiap.universidade.repository.PessoaRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class TesteController {

	@Autowired
	private DiscenteRepository rep;

	@Autowired
	private PessoaRepository repP;

	@GetMapping("/teste")
	public String retornarPagina() {
		return "pagina";
	}

	@GetMapping("/transfinfo1")
	public String transfInfo(HttpServletRequest req) {
		req.setAttribute("var", "World");
		return "pagina";
	}

	@GetMapping("/transfinfo2")
	public String transfInfo2(Model model) {
		model.addAttribute("var", "Brazil");
		return "pagina";
	}

	@GetMapping("/transfinfo3")
	public ModelAndView transfInfo3() {

		Pessoa p1 = new Pessoa(1L, "Fulano", "111.222.333-45", OpcoesPaises.BRASIL, 19);
		Discente d1 = new Discente(1L, "RM1234", p1, OpcoesStatus.ATIVO, OpcoesNivel.GRADUACAO);

		ModelAndView mv = new ModelAndView("pagina");
		mv.addObject("var", d1);

		return mv;

	}

	@GetMapping("/index1")
	public ModelAndView transfIndex() {

		/*
		 * Pessoa p1 = new Pessoa(1L, "Fulano", "111.222.333-45", OpcoesPaises.BRASIL,
		 * 19); Pessoa p2 = new Pessoa(2L, "Beltrano", "111.222.333-45",
		 * OpcoesPaises.BRASIL, 19); Pessoa p3 = new Pessoa(3L, "Joao",
		 * "111.222.333-45", OpcoesPaises.BRASIL, 19); Discente d1 = new Discente(1L,
		 * "RM1234", p1, OpcoesStatus.ATIVO, OpcoesNivel.GRADUACAO); Discente d2 = new
		 * Discente(2L, "RM4567", p2, OpcoesStatus.ATIVO, OpcoesNivel.GRADUACAO);
		 * Discente d3 = new Discente(3L, "RM8910", p3, OpcoesStatus.ATIVO,
		 * OpcoesNivel.GRADUACAO);
		 * 
		 * List<Discente> lista = new ArrayList<Discente>(); lista.add(d1);
		 * lista.add(d2); lista.add(d3);
		 */

		List<Discente> lista = rep.findAll();

		ModelAndView mv = new ModelAndView("index");
		mv.addObject("var", lista);
		return mv;

	}

	@GetMapping("/nova_pessoa")
	public ModelAndView novaPessoa() {
		
		ModelAndView mv = new ModelAndView("nova_pessoa");
		mv.addObject("lista_pais_origem", OpcoesPaises.values());
		mv.addObject("pessoa", new Pessoa());
		
		return mv;
		
		//return "nova_pessoa";
	}
	
	@GetMapping("/msg_erro_cad_pessoa")
	public String retornaMsgErroCadPessoa() {
		return "erro_cadastro_pessoa";
	}

	@PostMapping("/insere_pessoa")
	public ModelAndView inserePessoa(@Valid Pessoa pessoa1, BindingResult bd) {

		if (bd.hasErrors()) {
			//System.out.println("Tem erros");
			//return "redirect:/msg_erro_cad_pessoa";
			
			ModelAndView mv = new ModelAndView("nova_pessoa");
			mv.addObject("lista_pais_origem", OpcoesPaises.values());
			return mv;
			

		} else {
			Pessoa pes = new Pessoa();
			pes.setCpf(pessoa1.getCpf());
			pes.setIdade(pessoa1.getIdade());
			pes.setNome(pessoa1.getNome());
			pes.setPais_origem(pessoa1.getPais_origem());

			repP.save(pes);

			Discente dis = new Discente();
			dis.setNivel(OpcoesNivel.MBA);
			dis.setRm("RM4321");
			dis.setStatus(OpcoesStatus.FORMADO);
			dis.setPessoa(pes);

			rep.save(dis);

			return new ModelAndView("redirect:/index1");

		}

	}
	
	@GetMapping("/detalhes_discente/{id}")
	public ModelAndView exibirDetalhes(@PathVariable Long id) {
		
	Optional<Discente> op =	rep.findById(id);
	
	if(op.isPresent()) {
		
		Discente dis = op.get();
		ModelAndView mv = new ModelAndView("detalhes_discente");
		mv.addObject("discente", dis);
		return mv;
		
	} else {
		return new ModelAndView("redirect:/index1");
	}
		
		
	}
	@GetMapping("/atualiza_discente/{id}")
	public ModelAndView retornaAtualizaDiscente(@PathVariable Long id) {
		Optional<Discente> op = rep.findById(id);
		if(op.isPresent()) {
			ModelAndView mv = new ModelAndView("atualiza_discente");
			mv.addObject("discente",op.get());
			mv.addObject("lista_pais_origem", OpcoesPaises.values());
			mv.addObject("lista_niveis", OpcoesNivel.values());
			mv.addObject("lista_status",OpcoesStatus.values());
			return mv;
		}else {
			return new ModelAndView("redirect:/index1");
		}
	}
	
	@PostMapping("/atualizar_discente/{id}")
	public ModelAndView atualizaDiscente(@PathVariable Long id,@Valid Discente discente, BindingResult bd) {
		
		if(bd.hasErrors()) {
			ModelAndView mv = new ModelAndView("atualiza_discente");
			mv.addObject("discente",discente);
			mv.addObject("lista_pais_origem", OpcoesPaises.values());
			mv.addObject("lista_niveis", OpcoesNivel.values());
			mv.addObject("lista_status",OpcoesStatus.values());
			return mv;
		}else {
			Optional<Discente> op = rep.findById(id);
			if(op.isPresent()) {
				Discente dis = op.get();
				dis.setRm(discente.getRm());
				dis.setNivel(discente.getNivel());
				dis.setStatus(discente.getStatus());
				dis.getPessoa().setNome(discente.getPessoa().getNome());
				dis.getPessoa().setCpf(discente.getPessoa().getCpf());
				dis.getPessoa().setIdade(discente.getPessoa().getIdade());
				dis.getPessoa().setPais_origem(discente.getPessoa().getPais_origem());
				rep.save(dis);
				return new ModelAndView("redirect:/index1");
			}else {
				ModelAndView mv = new ModelAndView("atualiza_discente");
				mv.addObject("discente",discente);
				mv.addObject("lista_pais_origem", OpcoesPaises.values());
				mv.addObject("lista_niveis", OpcoesNivel.values());
				mv.addObject("lista_status",OpcoesStatus.values());
				return mv;
			}
		}
	}
	

	@GetMapping("/remove_discente/{id}")
	public String remover(@PathVariable Long id){
		Optional<Discente> op = rep.findById(id);
		if(op.isPresent()){
			rep.deleteById(id);
			return "redirect:/index1";
		}else{
			return "redirect:/index1";
		}
	}

}

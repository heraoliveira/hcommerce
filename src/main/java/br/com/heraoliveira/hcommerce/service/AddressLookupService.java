package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.model.Address;

public interface AddressLookupService {

    Address fetchAddress(String zip);
}
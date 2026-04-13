package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.models.Address;

public interface AddressLookupService {

    Address fetchAddress(String zip);
}
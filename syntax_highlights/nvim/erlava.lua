-- Erlava neovim plugin

local function setup()
    vim.cmd([[
        autocmd BufRead,BufNewFile *.lava set filetype=erlava
        autocmd Syntax lava runtime! syntax/erlava.vim
    ]])
end

return {
    setup = setup,
}
